package cn.itcast;

import com.hankcs.lucene.HanLPAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author lihai
 * @since 2021/2/2 0002
 */
public class QueryIndex {
    public void doQuery(Query query) throws IOException {
        //创建 索引的读 对象
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get("D:\\forProject\\luceneIndex")));
        //通过IndexSearcher对象执行查询
        IndexSearcher indexSearcher = new IndexSearcher(reader);

        TopDocs topDocs = indexSearcher.search(query, 10);
        //topDocs对象是查询结果包含 文档id的数组 和每个文档的得分
        //totalHits 文档的总命中数量
        int totalHits = topDocs.totalHits;
        System.out.println("文档的总命中数量为：=="+totalHits);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("当前文档的id================"+scoreDoc.doc);
            System.out.println("当前文档的得分=============="+scoreDoc.score);
            //使用文档读取对象 通过当前文档id获取 当前文档的document对象
            Document document = indexSearcher.doc(scoreDoc.doc);

            //通过document对象 从指定域中取数据
            System.out.println("文档的名称=="+document.get("fileName"));
//            System.out.println("文档的内容=="+document.get("fileContent"));
            System.out.println("文档的path=="+document.get("filePath"));
            System.out.println("文档的大小fileSize1=="+document.get("fileSize1"));
            System.out.println("文档的大小fileSize2=="+document.get("fileSize2"));
        }
    }

    /*
    * 查询全部
    * */
    @Test
    public void queryAll() throws IOException {
        //读取所有 的查询对象创建
        Query query = new MatchAllDocsQuery();
        doQuery(query);
    }

    /*
    * 词条查询
    * */
    @Test
    public void queryByTerm() throws IOException {
        //创建词条查询对象
//        TermQuery termQuery = new TermQuery(new Term("fileName", "spring"));
        TermQuery termQuery = new TermQuery(new Term("fileNum", "0000015"));

        doQuery(termQuery);
    }
    /*
    * 根据文档大小查询
    * */
    @Test
    public void queryBySize() throws IOException {
        Query query = LongPoint.newRangeQuery("fileSize1", 1L, 50L);

        doQuery(query);

    }
    /*
    * 组合查询
    * */
    @Test
    public void queryByBoolean() throws IOException {
        //创建词条查询对象
        TermQuery termQuery1 = new TermQuery(new Term("fileName", "传智播客"));
        TermQuery termQuery2 = new TermQuery(new Term("fileName", "不明觉厉"));

        //创建子查询对象
        BooleanClause booleanClause1 = new BooleanClause(termQuery1, BooleanClause.Occur.SHOULD);
        BooleanClause booleanClause2 = new BooleanClause(termQuery2, BooleanClause.Occur.SHOULD);

        BooleanQuery query = new BooleanQuery.Builder().add(booleanClause1).add(booleanClause2).build();
        doQuery(query);
    }
    /*
    * 单域字段分词查询
    * */
    @Test
    public void queryByStr() throws Exception {
        //1 定义关键字
        String queryStr = "传智播客优秀企业";
        //创建解析字符串的对象
        QueryParser queryParser = new QueryParser("fileName", new HanLPAnalyzer());
        Query query = queryParser.parse(queryStr);

        doQuery(query);
    }

    /*
    * 多域字段分词查询
    * */
    @Test
    public void queryByMultiField() throws Exception {
        //1.定义关键字作为查询
        String queryStr ="传智播客优秀企业";
        String[] fields = new String[]{"fileName","fileContent"};
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, new HanLPAnalyzer());
        Query query = parser.parse(queryStr);

        doQuery(query);
    }


}
