package cn.itcast;

import com.hankcs.lucene.HanLPAnalyzer;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author lihai
 * @since 2021/2/2 0002
 */
public class CreateIndex {

    public IndexWriter getIndexWriter() throws IOException {
        //指定索引存放的路径
        String indexDirectory = "D:\\forProject\\luceneIndex";
        //打开目录
        Directory directory = FSDirectory.open(Paths.get(indexDirectory));
        //创建分词器
        //标准分词器
//        Analyzer analyzer = new StandardAnalyzer();
        //创建hanlp分词器
        Analyzer analyzer = new HanLPAnalyzer();

        //创建索引配置信息对象
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        //创建索引写对象
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        return indexWriter;
    }

    /*
    * 创建索引库
    * */
    @Test
    public void createIndex(){
        try {
            //创建索引写对象
            IndexWriter indexWriter = getIndexWriter();
            //执行数据源的位置 即需要创建索引的文件夹位置
            File dir = new File("D:\\forProject\\searchsource");

            int i =1;
            //获取数据源数据
            for (File file : dir.listFiles()) {
                //文件名
                String fileName = file.getName();
                System.out.println("==============="+fileName);
                //文件内容
                String fileContent = FileUtils.readFileToString(file);
                System.out.println("==============="+fileContent);
                //文件路径
                String filePath = file.getPath();
                System.out.println("==============="+filePath);
                //文件大小
                long length = file.length();
                System.out.println("==============="+length);
                long fileSize = FileUtils.sizeOf(file);
                System.out.println("==============="+fileSize);

                /*
                * 域分类
                *   TextField     分词存储 支持索引查询 常用类型域字段
                *   StoredField   只做数据存储 不支持索引查询
                *   StringField   不分词 支持查询 用于存储唯一标识
                *   LongPoint     数值类型域字段 分词 查询 用于数值的范围查询 不存储在索引库
                * */
                //创建 文件名 域
                //参数1 域名
                //参数2 域的内容
                //参数3 是否存储
                Field fileNameField = new TextField("fileName", fileName, Field.Store.YES);
                //创建 文件内容 域
                Field fileContentField = new TextField("fileContent", fileContent, Field.Store.YES);
                //创建 文件路径 域  (StoreField类型域 只存储 不分析 不索引)
                Field filePathField = new StoredField("filePath",filePath);
                //创建 文件大小 域
                Field fileSizeField1 = new LongPoint("fileSize1",fileSize);
                Field fileSizeField2 = new StoredField("fileSize2", fileSize);

                //创建 文件编号 域
                Field fileNumField = new StringField("fileNum", "00000" + i, Field.Store.YES);

                //创建 document 对象
                Document document = new Document();

                document.add(fileNumField);
                document.add(fileNameField);
                document.add(fileContentField);
                document.add(filePathField);
                document.add(fileSizeField1);
                document.add(fileSizeField2);
                //创建索引, 并写入索引库
                indexWriter.addDocument(document);

                i++;
            }
            //提交
            indexWriter.commit();
            //关闭
            indexWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

    /*
    * 删除索引库
    * */
    @Test
    public void deleteIndex(){
        try {
            //创建索引写对象
            IndexWriter indexWriter = getIndexWriter();

            indexWriter.deleteAll();

            indexWriter.commit();
            indexWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateIndex() throws IOException {
        //创建索引写对象
        IndexWriter writer = getIndexWriter();

        Document document = new Document();

        document.add(new TextField("fileName","你好李焕英", Field.Store.YES));
        document.add(new TextField("fileContent","呵呵呵", Field.Store.YES));

        writer.updateDocument(new Term("fileNum","0000015"), document);
        writer.commit();
        writer.close();


    }
}
