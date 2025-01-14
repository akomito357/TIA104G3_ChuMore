package com.chumore.search.model;

import com.chumore.rest.model.RestService;
import com.chumore.rest.model.RestVO;
import com.chumore.review.model.ReviewService;
import com.chumore.util.analyzer.JiebaAnalyzer;
import com.huaban.analysis.jieba.JiebaSegmenter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@Service
public class LuceneIndexService {

//    @Value("${lucene.index.path}")
    private String luceneIndexDir;

    private final RestService restService;

    private final ReviewService reviewService;

    private Directory directory;
    private Analyzer analyzer;
    private IndexWriter writer;
    private IndexWriterConfig config;


    @Autowired
    public LuceneIndexService(RestService restService, ReviewService reviewService) throws IOException {
        this.restService = restService;
        this.reviewService = reviewService;
        init();
    }

    private void init() throws IOException{
        luceneIndexDir = "./data/lucene/index";

        if (luceneIndexDir == null || luceneIndexDir.isEmpty()) {
            throw new IllegalArgumentException("Lucene index directory is not configured!");
        }
        directory = FSDirectory.open(Paths.get(luceneIndexDir));
        analyzer = new JiebaAnalyzer(JiebaSegmenter.SegMode.INDEX);

        // 單例
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        writer = new IndexWriter(directory, config);
    }

    // 重建索引
    public void rebuildIndex() throws IOException {
        try{
            writer.deleteAll();
            List<RestVO> rests = restService.getAll();
            for(RestVO rest: rests){
                addOrUpdateDocument(rest);
            }
            writer.commit();
        } catch (IOException e) {
            writer.rollback();
            throw e;
        }
    }

    // 新增或更新Document
    public void addOrUpdateDocument(RestVO rest) throws IOException {
        Document doc = createDocument(rest);
        Term term = new Term("restId", rest.getRestId().toString());
        writer.updateDocument(term, doc);
    }

    private Document createDocument(RestVO rest) throws IOException {
        //建立 Lucene Document，填入需要索引的欄位
        Document doc = new Document();
        doc.add(new StringField("restId", rest.getRestId() != null ? rest.getRestId().toString() : "UNKNOWN", Field.Store.YES));
        doc.add(new TextField("restName", rest.getRestName() != null ? rest.getRestName() : "", Field.Store.YES));

        if(rest.getApprovalStatus()==0){
            // 如果 approvalStatus 為 0，僅索引必要欄位，跳過其他欄位
            doc.add(new IntPoint("approvalStatus", 0));
            doc.add(new StoredField("approvalStatusStored", 0));
        }else{
            doc.add(new IntPoint("approvalStatus", 1));
            doc.add(new StoredField("approvalStatusStored", 1));
            // 餐廳簡介
            doc.add(new TextField("restIntro", rest.getRestIntro() != null ? rest.getRestIntro() : "N/A", Field.Store.NO));

            // 料理類型
            if (rest.getCuisineType() != null) {
                doc.add(new TextField("cuisineTypeName", rest.getCuisineTypeName() != null ? rest.getCuisineTypeName() : "Unknown", Field.Store.NO));
            } else {
                doc.add(new TextField("cuisineTypeName", "Unknown", Field.Store.NO));
            }
            // 星星數 (用於範圍查詢與排序)
            if (rest.getRestStars() != null) {
                float restStars = rest.getRestStars().floatValue();
                doc.add(new FloatPoint("restStars", restStars));
                doc.add(new StoredField("restStarsStored", restStars));
                doc.add(new NumericDocValuesField("restStarsSort", Float.floatToIntBits(restStars)));
            } else {
                // 如果 restStars 為 null，設置默認值（如 0）
                doc.add(new FloatPoint("restStars", 0.0f));
                doc.add(new StoredField("restStarsStored", 0.0f));
                doc.add(new NumericDocValuesField("restStarsSort", Float.floatToIntBits(0.0f)));
            }


            // 評論數 (用於範圍查詢與排序)
            if (rest.getRestReviewers() != null) {
                int restReviewers = rest.getRestReviewers();
                doc.add(new IntPoint("restReviewers", restReviewers));
                doc.add(new StoredField("restReviewersStored", restReviewers));
                doc.add(new NumericDocValuesField("restReviewersSort", restReviewers));
            } else {
                // 如果 restReviewers 為 null，設預設值（如 0）
                doc.add(new IntPoint("restReviewers", 0));
                doc.add(new StoredField("restReviewersStored", 0));
                doc.add(new NumericDocValuesField("restReviewersSort", 0));
            }
        }

        // 城市和地址
        doc.add(new TextField("restCity", rest.getRestCity() != null ? rest.getRestCity() : "", Field.Store.NO));
        doc.add(new TextField("restDist", rest.getRestDist() != null ? rest.getRestDist() : "", Field.Store.NO));
        doc.add(new TextField("restAddress",
                (rest.getRestCity() != null ? rest.getRestCity() : "") +
                        (rest.getRestDist() != null ? rest.getRestDist() : "") +
                        (rest.getRestAddress() != null ? rest.getRestAddress() : ""), Field.Store.NO));


        //人均消費金額
//        BigDecimal avgCost = reviewService.getAverageCostByRestId(rest.getRestId());
//        float avgCostFloat = avgCost.floatValue();
//        doc.add(new FloatPoint("avgCost", avgCostFloat));
//        doc.add(new StoredField("avgCostStored", avgCostFloat));
//        doc.add(new NumericDocValuesField("avgCostSort", Float.floatToIntBits(avgCostFloat)));


        System.out.println(doc);
        return doc;

    }


    // 刪除指定餐廳的索引
    public void deleteDocument(Integer restId) throws IOException {
        try {
            System.out.println("Deleting document with restId: " + restId);
            writer.deleteDocuments(new Term("restId", restId.toString()));
            writer.commit();
        } catch (IOException e) {
            writer.rollback();
            throw e;
        }

    }

    @PreDestroy
    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
        if (directory != null) {
            directory.close();
        }
    }
}
