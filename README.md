文本分类多种算法的实现与效果测试
========



## 项目说明

+ 项目主要面向机器学习初学者或是各文本分类算法的效果测试者，下载项目后即可直接运行测试，查看各个分类算法以及中文分词的效果，效果由运行结果中的预测准确率体现；
+ 为了更清晰的看到各个分类算法的实现，项目没有引入其他机器学习方向的jar包，所有算法原理实现都在项目代码中查看即可。
+ 项目使用工厂模式组织，可以很方便的拓展分类器和分词器，并且查看效果。



## 材料说明

`./seeds`文件夹中包含了17个不同分类的训练集，每个分类都包含数百篇新闻（抓取自某新闻网站，同一分类取自该站点同一栏目下的文章），合计共6000多篇新闻。如需添加或替换新的训练集，只需按照同一层级放置文件即可。

`./dic`文件夹中包含一个中文词典文件，词典含有45万+个中文词语。



## 使用及部分代码说明

1. `com.fmyblack.ClassifyTest`为入口类，`main`方法中完成了将所有文本按一定比例随机分为训练集，测试集，使用工厂类获取对应的分类器，训练分类器，使用分类器测试测试集获得分类效果；
2. `com.fmyblack.ClassifierFactory`为工厂类，使用`getClassifyModel`方法组装分类算法和分词算法即可获得分类器；
3. 项目目前包含朴素贝叶斯（`com.fmyblack.textClassify.naiveBaye`），逻辑回归（`com.fmyblack.textClassify.lr`），余弦定理（`com.fmyblack.textClassify.cosine`）多个分类模型，也包含逆向最大匹配（`com.fmyblack.word.rmm`）多个分词算法，测试效果时可自由组装。



## 拓展说明

1. 如需添加新的分类算法，请继承`com.fmyblack.textClassify.ClassifyModel`接口；
2. 如需添加新的分词算法，请继承`com.fmyblack.word.WordSegmenter`接口；
3. 将新的算法在工厂`com.fmyblack.ClassifierFactory`中注册；
4. `com.fmyblack.textClassify.doc`包中实现了对训练集的一些基本操作，`com.fmyblack.textClassify.IDF`实现了idf算法，可供使用。
