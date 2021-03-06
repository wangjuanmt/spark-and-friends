# Introduction to Spark and friends

## Apache Spark
Apache Spark是一个开源集群运算框架，最初是由加州大学柏克莱分校AMPLab所开发。

相对于Hadoop的MapReduce会在运行完工作后将中介数据存放到磁盘中，Spark使用了存储器内运算技术，能在数据尚未写入硬盘时即在存储器内分析运算。

Spark在存储器内运行程序的运算速度能做到比Hadoop MapReduce的运算速度快上100倍，即便是运行程序于硬盘时，Spark也能快上10倍速度。

Spark允许用户将数据加载至集群存储器，并多次对其进行查询，非常适合用于机器学习算法。

使用Spark需要搭配集群管理员和分布式存储系统。Spark支持独立模式（本地Spark集群）、Hadoop YARN或Apache Mesos的集群管理。

在分布式存储方面，Spark可以和HDFS(Hadoop分布式文件系统)、 Cassandra (开源分布式NoSQL数据库系统)、OpenStack Swift和Amazon S3等接口搭载。

Spark也支持伪分布式（pseudo-distributed）本地模式，不过通常只用于开发或测试时以本机文件系统取代分布式存储系统。在这样的情况下，Spark仅在一台机器上使用每个CPU核心运行程序。

### Spark 构成要素
#### Spark核心和弹性分布式数据集（RDDs）
Spark核心是整个项目的基础，提供了分布式任务调度，调度和基本的I／O功能。而其基础的程序抽象则称为弹性分布式数据集（RDDs），

RDD, resilient distributed dataset, 是一个可以并行操作、有容错机制的数据集合。

RDDs可以透过引用外部存储系统的数据集创建（例如：共享文件系统、HDFS、HBase或其他 Hadoop 数据格式的数据源）。或者是透过在现有RDDs的转换而创建（比如：map、filter、reduce、join等等）。

##### Parallelized Collections
Parallelized collections are created by calling JavaSparkContext’s parallelize method on an existing Collection in your driver program.
The elements of the collection are copied to form a distributed dataset that can be operated on in parallel.

``````
List<Integer> data = Arrays.asList(1, 2, 3, 4, 5);
JavaRDD<Integer> distData = sc.parallelize(data);
``````

##### External Datasets
Spark can create distributed datasets from any storage source supported by Hadoop, including your local file system, HDFS, Cassandra, HBase, Amazon S3, etc.
Spark supports text files, SequenceFiles, and any other Hadoop InputFormat.

``````
JavaRDD<String> distFile = sc.textFile("data.txt");
``````
or
``````
JavaRDD<String> peopleRDD = spark.sparkContext()
      .textFile("src/main/resources/people.txt", 1)
      .toJavaRDD();
``````
Some notes on reading files with Spark:
* If using a path on the local filesystem, the file must also be accessible at the same path on worker nodes.
Either copy the file to all workers or use a network-mounted shared file system.

* All of Spark’s file-based input methods, including textFile, support running on directories, compressed files, and wildcards as well.
For example, you can use textFile("/my/directory"), textFile("/my/directory/*.txt"), and textFile("/my/directory/*.gz").

* The textFile method also takes an optional second argument for controlling the number of partitions of the file.
By default, Spark creates one partition for each block of the file (blocks being 128MB by default in HDFS),
but you can also ask for a higher number of partitions by passing a larger value.
Note that you cannot have fewer partitions than blocks.

Apart from text files, Spark’s Java API also supports several other data formats:

* JavaSparkContext.wholeTextFiles lets you read a directory containing multiple small text files, and returns each of them as (filename, content) pairs.
This is in contrast with textFile, which would return one record per line in each file.

* For SequenceFiles, use SparkContext’s sequenceFile[K, V] method where K and V are the types of key and values in the file.
These should be subclasses of Hadoop’s Writable interface, like IntWritable and Text.

* For other Hadoop InputFormats, you can use the JavaSparkContext.hadoopRDD method, which takes an arbitrary JobConf and input format class, key class and value class.
Set these the same way you would for a Hadoop job with your input source.
You can also use JavaSparkContext.newAPIHadoopRDD for InputFormats based on the “new” MapReduce API (org.apache.hadoop.mapreduce).

* JavaRDD.saveAsObjectFile and JavaSparkContext.objectFile support saving an RDD in a simple format consisting of serialized Java objects.
While this is not as efficient as specialized formats like Avro, it offers an easy way to save any RDD.

##### RDD Operations
[RDD Operations](https://spark.apache.org/docs/latest/rdd-programming-guide.html#rdd-operations)
RDDs support two types of operations: transformations, which create a new dataset from an existing one,
and actions, which return a value to the driver program after running a computation on the dataset.
For example, map is a transformation, and reduce is an action.

All transformations in Spark are _lazy_, in that they do not compute their results right away.
Instead, they just remember the transformations applied to some base dataset (e.g. a file).
The transformations are only computed when an action requires a result to be returned to the driver program.

RDD抽象化是经由一个以Scala, Java, Python的语言集成API所呈现，简化了编程复杂性，应用程序操纵RDDs的方法类似于操纵本地端的数据集合。

#### Spark SQL
Spark SQL在Spark核心上带出一种名为SchemaRDD的数据抽象化概念，提供结构化和半结构化数据相关的支持。Spark SQL提供了领域特定语言，可使用Scala、Java或Python来操纵SchemaRDDs。它还支持使用使用命令行界面和ODBC／JDBC服务器操作SQL语言。在Spark 1.3版本，SchemaRDD被重命名为DataFrame, 即DataSet<Row>。

#### Spark Streaming
Spark Streaming充分利用Spark核心的快速调度能力来运行流分析。它截取小批量的数据并对之运行RDD转换。这种设计使流分析可在同一个引擎内使用同一组为批量分析编写而撰写的应用程序代码。

#### MLlib
MLlib是Spark上分布式机器学习框架。Spark分布式存储器式的架构比Hadoop磁盘式的Apache Mahout快上10倍，扩充性甚至比Vowpal Wabbit要好。MLlib可使用许多常见的机器学习和统计算法，简化大规模机器学习时间，其中包括：
* 汇总统计、相关性、分层抽样、假设检定、随机数据生成
* 分类与回归：支持向量机、回归、线性回归、逻辑回归、决策树、朴素贝叶斯
* 协同过滤：ALS
* 分群：k-平均算法
* 维度约减：奇异值分解（SVD），主成分分析（PCA）
* 特征提取和转换：TF-IDF、Word2Vec、StandardScaler
* 最优化：随机梯度下降法（SGD）、L-BFGS

#### GraphX
GraphX是Spark上的分布式图形处理框架。它提供了一组API，可用于表达图表计算并可以模拟Pregel抽象化。GraphX还对这种抽象化提供了优化运行。
GraphX最初为加州大学柏克莱分校AMPLab和Databricks的研究项目，后来捐赠给Spark项目。


## hive
hive是基于Hadoop的一个数据仓库工具，通过一种类SQL语言HiveQL为用户提供数据的归纳、查询和分析等功能。可以将结构化的数据文件映射为一张数据库表，并提供简单的SQL查询功能，可以将SQL语句转换为MapReduce任务进行运行。其优点是学习成本低，可以通过类SQL语句快速实现简单的MapReduce统计，不必开发专门的MapReduce应用，十分适合数据仓库的统计分析。
[Language Manual](https://cwiki.apache.org/confluence/display/Hive/LanguageManual)

