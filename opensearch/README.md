# 阿里云开放搜索
* [管控台](https://opensearch-cn-shanghai-in.console.aliyun.com/#/apps)
* [官方文档](https://help.aliyun.com/document_detail/29119.html)
* [java-sdk](https://help.aliyun.com/document_detail/52286.html)
* 一个应用下一般建一个表，可以走数据源同步，也可以通过sdk上传。
    * 通过sdk上传方式，先将id插入到tmp表中，再从tmp中获取数据，拼装接口上传。
        * 此方案可以设置重试次数。
        * 如果数据库中多张表需要联合查询，适合此方案。
        * API 每次推送总文档数：API 每次推送总文档数	1000个，建议100个性能更好（建议打包推送）
* 新高级版：500次/每秒，编码前2M/每秒，单条文档大小1M
* 一般应用 40G，200次/秒基本够用。 具体看各自业务需求。
* 后台可以进行搜索测试
* 一般情况下会新建tmp表，然后定时任务从tmp表取出id，再封装数据结构，推送文档。
    * 失败后可以加上重试机制。
    * 可以批量推送，建议每个文档最少推送3次，保证

### 创建表
* 数据类型使用  *_ARRAY 类型，如 LITERAL_ARRAY，INT_ARRAY，具体见[文档](https://help.aliyun.com/document_detail/29121.html)
* LITERAL 类型不分词，一般为全匹配，如手机号，邮箱等。
* 时间类型可以转为int(int64)类型。
* title等需要分词的可以选择text类型。


### 文档操作 [3.1.3](https://help.aliyun.com/document_detail/52286.html)
* accessKey和accessSecret见阿里云账号中心
* 操作类型
    * ADD 为添加，如果存在则全量更新。
        * 标准版不支持update,需要使用ADD进行全字段更新；高级版支持update,部分字段更新。
        * com.kk.opensearch.util.JsonUtil.toJsonString 全量更新
    * UPDATE  部分字段更新
        * JSON.toJSONString()
        * 如果同步过来的增量文档主键值在对应应用表中不存在，老高级版应用会转ADD，新高级版会报错。
    * DELETE  删除文档
* [相关性函数-位置查询](https://help.aliyun.com/document_detail/29131.html)
* 查询
    * 最多查询5000条数据。
        * start+hit<=5000，超过5000会直接报错无结果。
    * [Query](https://help.aliyun.com/document_detail/29157.html)
        * 查询字段必须建立索引
        * 等于查询，用 AND 隔开，如 id:'2' AND creator:'k2'
        * 查询条件可以为多个，且支持关系为：()、AND、OR、ANDNOT、RANK。(必须为大写)
        * 支持数组，TEXT等类型。filter不支持。
        * 关键词查询必须用单（双）引号括起来，否则会导致报错无结果或者行为不可预期。
            * text 类型单引号和双引号区别：
                * 查询title索引字段中包含“北京大学”的文档；
                    * query=title:'北京大学'
                * 查询title索引字段中包含“北京大学”的文档，要求“北京大学”不能分开，不希望返回类似“北京的大学”的文档；
                    * query=title:"北京大学"
    * [filter](https://help.aliyun.com/document_detail/29158.html)
        * filter中 数值类型不使用引号，LITERAL 使用双引号，不支持Text类型。且必须是属性字段,见应用结构配置。
        * filter 通过列名查询，query通过索引查询。
        * float、double类型因为精度问题无法做精确相等的判断，如有这种场景请，如有这种场景请改用<及>来实现。
        * literal类型的字段值，在filter子句中必须要加双引号（否者会报6135 常量表达式类型错误），支持所有的关系运算，不支持算术运算。
        * 排序特征function函数也可以在filter子句中使用；
        * literal类型字段的过滤仅支持=、!=运算，含义为等于、不等于，不支持>、<等关系运算。（literal字段类型不分词，需要完全匹配）
        * in/notin 判断字段值是否（不）在指定列表中，只支持INT及FLOAT类型，( 不支持ARRAY及LITERAL、TEXT、模糊分词系列类型 )，详细用法参考 搜索相关性函数 中的描述。
            * [in/notin文档](https://help.aliyun.com/document_detail/51260.html)
            * SearchHelper中可以模拟in操作，通过 OR 来实现，可以支持 ARRAY 和 LITERAL 类型，但是仍旧无法支持TEXT类型。
        * 分词字段类型无法配置为属性，例如 TEXT，SHORT_TEXT等都不支持，只支持数值字段类型及不分词字段类型配置为属性，例如int，int_array，float，float_array，double，double_array，literal，literal_array这些字段类型都支持。
    * [sort](https://help.aliyun.com/document_detail/29159.html)
        * array类型字段均不支持排序
* [下拉提示](https://help.aliyun.com/document_detail/74272.html)
    * [api](https://help.aliyun.com/document_detail/52364.html)


