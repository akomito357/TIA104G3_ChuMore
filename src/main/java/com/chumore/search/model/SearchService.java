package com.chumore.search.model;

import com.chumore.dailyreservation.model.DailyReservationService;
import com.chumore.util.analyzer.JiebaAnalyzer;
import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 1. 功能：整合 Lucene 索引查詢與 dailyReservation (剩餘桌數) 的二次篩選。
 * 2. 搜尋結果依照自訂排序 (星星數, 評論數, 人均消費)。
 */

@Service
public class SearchService {

//    @Value("${lucene.index.path}")
    private String luceneIndexDir;

    private Directory directory;
    private Analyzer analyzer;
    private DirectoryReader directoryReader;
    private IndexSearcher searcher;


    private final DailyReservationService dailyReservationService; // 用於檢查剩餘桌數

    @Autowired
    public SearchService(DailyReservationService dailyReservationService) throws IOException{
        this.dailyReservationService = dailyReservationService;
        init();
    }

    public void init() throws IOException {
        luceneIndexDir = "./data/lucene/index";
        if(luceneIndexDir == null || luceneIndexDir.isEmpty()){
            throw new IOException("Lucene index directory is not configured!");
        }

        directory = FSDirectory.open(Paths.get(luceneIndexDir));
        analyzer = new JiebaAnalyzer(JiebaSegmenter.SegMode.SEARCH);

        directoryReader = DirectoryReader.open(directory);
        searcher = new IndexSearcher(directoryReader);
    }


    /*
        步驟：
        1. Lucene搜尋：doLuceneSearch(keyword) 執行基於關鍵字的多欄位模糊搜尋，並依照自訂排序規則排序，取得初步符合條件的餐廳ID list
        2. 訂位篩選：filterByDailyReservations(luceneResultIds, date, time, people)，根據指定的日期、時間和人數，篩選出剩餘桌數足夠的餐廳 ID，取得最終符合條件的餐廳ID list

     */

    // 搜尋符合條件的餐廳
    public List<Integer> searchRests(String keyword, LocalDate reservationDate,Integer reservationTime, Integer guestCount) throws Exception{
        // 1. 使用 Lucene 執行關鍵字搜尋，取得初步的餐廳 ID 列表
        List<Integer> restIds = doLuceneSearch(keyword);
        System.out.println("初步搜尋結果: " + restIds);
        // 2. 根據每日訂位資料進行剩餘桌數的篩選，取得最終符合條件的餐廳 ID 列表 (filterByDailyReservations)
        List<Integer> finalResultIds = dailyReservationService.filterRestIdsByConditions(restIds,reservationDate, reservationTime, guestCount);
        System.out.println("最終搜尋結果: " + finalResultIds);
        return finalResultIds;
    }

    // 搜尋符合關鍵字的餐廳
    private List<Integer> doLuceneSearch(String keyword) throws Exception{

        List<Integer> resultList = new ArrayList<>();

        // 定義多欄位搜尋
        String[] fields = {"restName","restIntro","restCity","restAddress","cuisineTypeName"};
        // 設定權重
        Map<String,Float> boosts = new HashMap<>();
        boosts.put("restName",3.0f);
        boosts.put("restAddress",2.0f);
        boosts.put("cuisineTypeName",1.5f);
        boosts.put("restIntro",1.0f);

        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer,boosts);

        // 若無輸入關鍵字則搜尋所有餐廳
        if(keyword==null || keyword.trim().isEmpty()){
            keyword = "*";
        }

        System.out.println("搜尋關鍵字: " + keyword);

        parser.setDefaultOperator(QueryParser.Operator.AND);


        // 支援模糊查詢，使用 ~2 表示模糊查詢的允許誤差；處理跳脫字元
        Query mainQuery = parser.parse(QueryParser.escape(keyword)+"~1");

        // 加入 approval status = 1 的篩選條件
        Query approvalQuery = IntPoint.newExactQuery("approvalStatus", 1);
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        booleanQuery.add(mainQuery, BooleanClause.Occur.MUST);
        booleanQuery.add(approvalQuery,BooleanClause.Occur.FILTER);

        // 定義排序規則： restStars DESC, restReviewers DESC, avgCost ASC

        Sort sort = new Sort(
                new SortField("restStarsSort",SortField.Type.INT,true), // 倒序
                new SortField("restReviewersSort",SortField.Type.INT,true)
//                new SortField("avgCostSort",SortField.Type.INT,false)
//                ,SortField.FIELD_SCORE
        );

        // 執行搜尋
        TopDocs topDocs = searcher.search(booleanQuery.build(),100,sort);
        System.out.println("搜尋結果數量: " + topDocs.totalHits.value);
        for(ScoreDoc sd : topDocs.scoreDocs){
            Document doc = searcher.doc(sd.doc);
            String restIdStr = doc.get("restId");

            Explanation explanation = searcher.explain(booleanQuery.build(), sd.doc);
            System.out.println("餐廳ID: " + restIdStr);
            System.out.println("餐廳名稱：" + doc.get("restName"));
            System.out.println("評分: " + sd.score);
            System.out.println("詳細評分解釋: \n" + explanation.toString());
            resultList.add(Integer.valueOf(restIdStr)); // 餐廳ID列表
        }

       return resultList;
     }


    @PreDestroy
    public void close() throws IOException {
        if(directoryReader != null){
            directoryReader.close();
        }
        if(directory != null){
            directory.close();
        }
    }





}
