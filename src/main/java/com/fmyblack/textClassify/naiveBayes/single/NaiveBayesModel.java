package com.fmyblack.textClassify.naiveBayes.single;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.fmyblack.common.FileReaderUtil;
import com.fmyblack.textClassify.tfidf.Document;
import com.fmyblack.textClassify.tfidf.TagBase;
import com.fmyblack.word.rmm.Rmm;

public class NaiveBayesModel{

	List<TagBase> tags;
	TFIDF tfidf;
	Rmm rmm;
	static int idfPower = 3;
	static double lambda = 0.0000001;
	
	private NaiveBayesModel(){
		rmm = Rmm.getIns();
	};
	
	public static NaiveBayesModel train(String seedsDir) {
		NaiveBayesModel nbm = new NaiveBayesModel();
		
		Seeds seeds = new Seeds(seedsDir);
		nbm.tags = seeds.getTags();
		int documentsNum = seeds.getDocumentsNum();
		Set<String> words = seeds.getWords();
		nbm.tfidf = new TFIDF(documentsNum, words);
		nbm.tfidf.fit(nbm.tags);
		
//		for(Tag tag : nbm.tags) {
//			System.out.println(tag.toString());
//		}
		
		return nbm;
	}
	
	public Result classify(String text) {
		List<String> words = rmm.rmmSegment(text);
		Document doc = new Document(words);
		Map<String, Integer> wordsFre = doc.getWordsFrequency();
		Result currentResult = null;
		double maxAccuracy = 0.0;
		Set<String> trainWords = this.tfidf.getWords();
		for(TagBase tagBase : this.tags) {
			Tag tag = (Tag) tagBase;
			Result tmpResult = tag.caculateAccuracy(wordsFre, trainWords);
			System.out.println(tmpResult);
			if(tmpResult.getAccuracy() > maxAccuracy) {
				currentResult = tmpResult;
				maxAccuracy = currentResult.getAccuracy();
			}
		}
		currentResult.setAccuracy(maxAccuracy / words.size() * 100);
		return currentResult;
	}
	
	public void save(String fileName) {
		File file = new File(fileName);
		if(file.exists()) {
			file.delete();
		}
		try {
			FileWriter fw = new FileWriter(fileName, true);
			this.tfidf.save(fw);
			for(TagBase tagBase : tags) {
				Tag tag = (Tag) tagBase;
				tag.save(fw);
			}
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
	}
	
	public static NaiveBayesModel load(String fileName) {
		NaiveBayesModel nbm = new NaiveBayesModel();
		nbm.tags = new ArrayList<TagBase>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName));
			String readString = null;
			if(( readString = reader.readLine()) != null) {
				TFIDF tfidf = (TFIDF) TFIDF.load(readString);
				nbm.tfidf = tfidf;
			}
			while(( readString = reader.readLine()) != null) {
				Tag tag = Tag.load(readString);
				nbm.tags.add(tag);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
		}
		return nbm;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		String dir = "/Users/fmyblack/javaproject/textClassify/src/main/resources/data";
		String seedsDir = dir + File.separator + "nbc_seeds";
		String testDir = dir + File.separator + "test_data";
		String resultOut = dir + File.separator + "result.out";
//		NaiveBayesModel nbm = NaiveBayesModel.train(seedsDir);
//		nbm.save(resultOut);
//		
		NaiveBayesModel nbm = NaiveBayesModel.load(resultOut);
//		
//        test(nbm, seedsDir);
//        testNew(nbm, testDir);
		testNewFile(nbm);
	}
	
	public static void testNewFile(NaiveBayesModel nbm) {
		String text = ""
//				+ "贫困地区发展特色产业促进精准脱贫指导意见"
//				+ "国家统计局于22日发布了31个省份2016年的地区生产总值(GDP)数据。"
//				+ "从经济总量上来看，广东、江苏不出意外地牢牢占据着排名第一、第二的位置，不过广东的第一经济大省位子更稳了;在名义增速方面，则呈现了南快北慢的趋势，西南地区的西藏、贵州和重庆均超过了11%，位列前三。"
//				+ "值得一提的是，在“挤水分”后，辽宁在全国的GDP排名，从之前的第10名下滑到14名，排在了中部省份安徽的后面。"
//				+ "1月17日，辽宁省委副书记、省长陈求发在辽宁省十二届人大八次会议上作政府工作报告时首次确认，辽宁省所辖市、县，在2011年至2014年存在财政数据造假的问题，指出在2011年至2014年“官出数字、数字出官”，导致经济数据被注入水分。"
//				+ "辽宁省财政厅数据显示，2015年全省一般公共预算收入2125.6亿元，下降33.4%，其中税收收入1650.2亿元，下降29.2%;非税收入475.4亿元，下降44.9%。辽宁省财政厅当时对此的解释是，造成全省财政收入大幅下降的主要原因中，排在首位的是按“三严三实”依法依规组织财政收入，做实了财政收入的影响。";
//				+ "简章规定，从今年开始，国际学生不需要考试，只要通过HSK汉语水平测试，就可以申请清华大学本科。而以往所有申请清华本科的国际学生，都需要先参加4至5个科目的考试，然后再加试或面试后录取。"
//				+ "网友为此惊呼：“十年寒窗不如一纸国籍，清华新政让中国考生欲哭无泪！”人们担心，清华为外籍学生设置的入学门槛太低，对国内考生不公平。更担心的是，清华新政一出，有条件的家长，完全可以让子女放弃中国国籍，以“移民”的方法曲线上清华。"
//				+ "据报道，很多东南亚或非洲小国移民中介机构，已经打出了“免试上清华”的宣传口号，提供整体解决方案。"
//				+ "写写"
//				+ "<p>　　蓝皮书指出，2009年中国经济增长虽然仍存在一定的下行压力，但“保八”的经济增长目标基本可以实现。在对2009年我国主要国民经济指标进行预测时，蓝皮书指出，预计2009年我国GDP增长速度将达到8.3%左右，增长速度有所放缓。</p>"
//				+ "<p>　　王方华表示，这一决定主要因为有理工科背景、掌握了较强逻辑推理能力的学生更符合管理学培养的要求，而且这样的学生更受企业欢迎。王方华透露，目前国务院学位办正在征求意见，看能否形成一个应用型的工商管理学士、工商管理硕士、工商管理博士的统一体系。</p> <p>　　王方华说，高中毕业生对专业的理解受父母和社会影响较大，没有自己的独立判断能力。当学生接受了两年的本科教育，在大三贴近就业的时机，作出的选择会更具合理性。</p> <p>　　这一决定意味着上海交大任何理工科专业的新生都有机会在两年以后转读管理专业，“管理学院敞开大门欢迎那些有志于从事管理的学生。”王方华说。</p>"
//				+ "热点栏目 <p> </p> <p>　　新股（300539）今日首次打开一字涨停板，截止（09：32），最新报价为46.03元，较发行价上涨652.12%。 该股8月30日上市，首日如期上涨44%之外，随后连获17个一字涨停板。</p> <p>　　横河模具发行价6.12元，发行市盈率22.96倍，发行股份数量为2375.00万股，其中网上发行945.00万股，占本次发行总量的39.79%。公司主营业务为模具、塑料制品（除饮水桶）、电子元件、五金配件、家用电器及配件制造、加工；自营和代理货物和技术的进出口，但国家限定经营或禁止进出口的货物和技术除外。</p> <p>进入讨论</p>"
//				+ "今天中午12点左右，太湖大桥一号桥桥面上发生一起严重交通事故。</p><p>事故车辆中一辆是58路公交车，另一辆是上海牌照大客车，初步统计现场有七、八人受伤，含重伤两人。据度假区警方现场证实，事故车辆是迎面相撞，公交车逆行行驶，现场车损十分严重，目前现场已经封闭，路面拥堵。";
//				+ "<p><img src=\"http://cb.uar.hubpd.com/spider_data/f890/f890510b2394cb44e8fb0952ddfad3ce.jpg\">"
				+ "</p> <p>安健逸馨养怡院老人在用餐。（陈朝霞 摄）</p> <p>　　每天上班前，王蓉步行来到海曙区汪弄社区微型养老院——安健逸馨养怡院，"
				+ "陪伴住在养怡院里的母亲吃早饭，并跟母亲约定晚上来陪她散步。从家出发到养怡院只需要5分钟，"
				+ "王蓉和母亲很享受这样可以陪伴但又不相互干扰的生活。</p> <p>　　随着老年社会的来临，"
				+ "养老问题成为全社会日益关心的问题。建在社区中的安健逸馨养怡院是海曙区建立“嵌入式”微型养老院的一种尝试。它有没有市场？"
				+ "老人们愿不愿意住？运营有无难处？</p> <p>　　家门口便利的养老中心</p> <p>　　"
				+ "所谓“嵌入式”微型养老院，是指利用社区中的闲置公建配套设施，"
				+ "通过引入社会资源实现老人“在家门口养老”的愿望。和大型的传统养老院不同，"
				+ "“嵌入式”养老选择场地、服务更加灵活。</p> <p>　　安健逸馨养怡院有床位36张。"
				+ "这里每个房间虽然小，但服务设施齐全，空调以及康复理疗设备等一应俱全。"
				+ "在这里，闲暇时，几位老人穿着休闲，坐在一起聊天、看电视。闻到电饭煲里飘出米饭的香味时，"
				+ "老人们知道要开饭了。</p> <p>　　“在这儿住着有家的感觉，要是想回家看看，走几分钟也就到了。”"
				+ "已经在这里住了三年的陈奶奶说。陈奶奶老家在合肥，今年86岁，跟着女儿来到了宁波，但因为不习惯和年轻人住在一块，"
				+ "后来搬住进了养怡院。刚来时，她还有所顾虑，但住了三天，就打算长久住下来。“我之前血糖很高，住在这里调养后，"
				+ "血糖低下去稳定了。”陈奶奶说。</p> <p>　　养怡院常务副院长王梅娥说，与别的养老院不同，"
				+ "为了让老人有更好的饮食，这里安排一日5餐，除了早中晚3餐，还为老人准备了水果和点心。"
				+ "在均衡的饮食和营养搭配下，老人们的身体得到了有效调理。王梅娥还牵头建起了一个微信群，"
				+ "将家属和工作人员拉进群里，每天发送老人当天活动的照片、吃饭的菜谱等内容。家属有什么要求，"
				+ "也可以在群里畅所欲言。西门街道老年合唱队在家门口的养老中心排练。（陈朝霞 摄）</p> <p>　　有助于缓解城区养老难题</p> <p>　　“虽然我们规模较小，但各项服务并没有打折，护工、厨师一个不少，老人能享受到相对专业化和个性化的服务。”王梅娥说，院里有一名长期的义工王培红，曾是妇儿医院老年科护士长，更去各个养老院做义工，积累了丰富的为老服务经验。</p> <p>　　王梅娥介绍，目前院里共有25位老人居住，每位老人的收费大约是每月3000元。从盈利角度上看，这样的营收状况是养活不了机构的，他们看重的是社区的辐射功能。“我们希望依托社区养老服务站点辐射到居家养老人群，最大化利用资源。”王梅娥说，比如家庭照料难度较高的翻身、清洁以及康复训练，可以通过服务人员直接上门或是老人进入机构做康复的方式进行，一方面惠及社区更多的老人，另一方面也盘活了养老资源。</p> <p>　　而对托养老人来说，最看重的就是离家近，子女可以随时来探望。“探望非常方便，就像是自己的第二个家。”不少家属这么认为。</p> <p>　　“主城区寸土寸金，现有养老机构基本上‘一床难求’，而大的民办养老院要么离家太远、要么太贵。这种‘嵌入式’微型养老院，由于就建在社区里，满足了老人和家属的需要，有助于缓解主城区养老难题。”海曙区民政局副局长吴鹤立如是说。</p> <p>　　微型养老院推广尚需时日</p> <p>　　目前，海曙区共有15.3万名老人，占比24.5%，80岁以上的老人有2.4万，占比15.3%，老龄化越来越严重，而养老市场也成为一个蓝海，吸引创业者纷纷加入。</p> <p>　　“选址的确是一个问题。”王梅娥坦言，“安健”刚起步时只是一家家政公司的下属机构，1年前才去民政局申办了资质。在环评阶段时，王梅娥担心了很久，“受传统观念影响，不少居民对这个新生事物起初有抵触情绪，甚至把这里当成了临终关怀场所，认为有病重的老人住在自己周围不吉利。如果有居民闹意见，我们就很难通过。”王梅娥说，“很感谢，居民们最后都给予理解和支持，顺利通过了环评。”</p> <p>　　另外，消防问题也制约微型养老院发展。“微型养老院由于规模小，按照消防对养老院的要求，很难达标，这也是‘嵌入式’养老院在社区受欢迎，但很难推广的一个重要原因。”吴鹤立分析。</p> <p>　　在海曙区2017年召开的民政工作会议中，提出了要借助市场力量，鼓励建办微型养老院。目前，鼓楼即将和万科合作开设街道级别的微型养老院，为更多的老人提供家门口的养老服务。</p> <p>责任编辑：陈琰 SN225</p>";
//				+ "编者按：讲真话，是做人品质，也是党性要求。党的十八届六中全会指出，党的各级组织和全体党员必须对党忠诚老实、光明磊落，说老实话、办老实事、做老实人，如实向党反映和报告情况，反对搞两面派、做“两面人”，反对弄虚作假、虚报浮夸，反对隐瞒实情、报喜不报忧。可见，党内政治生活中，共产党员敢讲真话、愿讲真话，本质是忠诚，要义在老实，也是爱党、护党、为党的具体体现。本期“修养视点”的三篇文章，围绕怎样讲真话和老实做人做事，与读者探讨交流，形成共识。 党内应该“打开天窗说亮话” ■刘师苑 “假话全不说、真话不全说”，有人以此为做人处世的信条。坚持“假话全不说”，这是很高的道德底线；主张“真话不全说”，用之于处世智慧则可，但对于共产党员而言，在党内政治生活中则不可有“真话不全说”。 党内政治生活中“真话不全说”，会贻害事业、耽误同志。试想，如果对单位建设存在的突出问题闪烁其词，说一半留一半，就可能有过半的问题得不到及时有效解决；如果对身边同志背离党性的言行含糊其词，重的不讲拣轻的讲，就可能使身边同志认识不到错误，在犯错甚至犯罪的道路上一步步滑向深渊；如果在研究重大问题时不将掌握的实情和盘托出，信息失真必然导致决策失误。所以，党内政治生活中“真话不全说”，既不符合优良传统，也是对组织、对同志和对自己的不负责。 对共产党员而言，应该是无话不对党说，这也是一名党员对党绝对忠诚的体现。现在，有些党员在党内政治生活中讲真话有保留，看起来是顾忌说话的环境、上级的态度、同志的感受，实则是一种趋利避害。“逢人且说三分话”，丢了的恰恰是对组织和同志的“一片心”。古人尚且能“富贵不能淫，贫贱不能移，威武不能屈”，我们又岂能汲汲于一己之私？从入党第一天起，我们就把自己的一切交给了党，如果纠结于个人一点私利、一份偏执，连真话、心里话都不敢对组织“竹筒倒豆子”痛快讲，那还谈什么忠诚？ 党员领导干部肩上有千钧重担，身后有千军万马，是强军兴军的骨干力量，在强调讲真话这个问题上必须标准更高，以身示范，做到在党内知无不言、言无不尽。一则补“钙”。“理想信念是共产党人精神上的‘钙’”。当前，要深入系统地学习习主席系列重要讲话，坚定看齐追随，时刻不忘初心，让理想信念的“钙”密度更高，讲真话、报实情的底气更足，求实崇真的作风更加过硬。二则砺“胆”。“千夫诺诺，不如一士谔谔”。要带头坚守党性，深入实际而不浮躁，独立思考而不跟风，敢讲真话而不人云亦云，如实向党反映和报告情况，不隐瞒自己的观点和态度，畅所欲言、敢于直言，这也正是共产党人赤胆忠心的人格魅力所在。三则静“心”。淡泊名利，管住欲望，时刻保持感恩心、平常心、进取心，做到心不动于利益、目不眩于诱惑，方能“物物而不物于物”，不让名利之诱使真话梗于喉，不因五色之惑把实情咽进肚。 一位哲人说过，一个人从另一个人的诤言中所得来的光明，比从他自己的理解力、判断力中得来的光明更干净、纯粹。对同志如此，对组织亦然。领导干部应带头培育“敢说实话、道尽真话”的土壤，让敢于发声、敞开心扉的人得到褒奖，让耍小聪明、藏着掖着的人受到批评，让“打开天窗说亮话”在党内政治生活中蔚成风气。当然，讲真话也应考虑方式方法，使人容易接受，这样才能达到促进事业发展、保持党组织纯洁的目的。 （作者单位：无锡联勤保障中心） 实践要义在于“老实” ■廖福安 我们党一贯倡导“说老实话、办老实事、做老实人”，这也是共产党员做人、说话、办事应遵循的原则和标准。 说老实话、办老实事、做老实人，关键之点是“实”。实，是唯物主义的世界观和方法论。它要求做一切工作必须从实际出发，必须坚持实践第一的观点，必须要有务实的工作作风。如此，才能获取真知，工作才能脚踏实地，一步一个脚印，开出实花结出硕果。实现中华民族伟大复兴，任务不可谓不重，困难不可谓不大。而战胜困难，实现目标，必须坚持一切从实际出发，说老实话、办老实事、做老实人。这是取胜之道、制胜之宝。 实的对立面是虚、假、浮。虚者，只空谈，不实干。常常表现为，纸上写了，墙上贴了，嘴上也说了，可就是不见行动，“只闻雷声不见雨”。结果可想而知，既不会开花更不会结果。假者，言不由衷，言不由实，言行不一，以假代真。常常表现为，把问题美化为成绩，把平庸夸大为优秀，把小成绩说成是大业绩，等等。如此说假话，造成的危害是不可估量的；浮者，漂于上边，远离实际。常常表现为，身入基层而没有深入基层，做做样子而不是做好样子，浅尝辄止而不去下真功夫，等等。以上种种表现，是“实”的大敌，不坚决纠正，工作就落不到实处，只会落到虚处、空处。 干部形象也是干事导向。作为党员领导干部，更重要的是靠自身良好形象感染和影响群众。群众衡量干部形象的标准主要有两个，一个是“听其言”，一个是“观其行”。如果言为老实话，行为老实事，做到言为心声，表里如一，群众就会在心中给你画个好形象。周恩来同志说过，世界上最聪明的人是最老实的人。这话很有哲理，因为获取知识，增长本领，干好本职工作，赢得组织和群众信任，都需要老老实实。 也有人认为，做老实人吃亏。其实，这种观点失之偏颇，如果全面地加以分析，就会得出另一个结论：做老实人并不吃亏。一者，个人虽然吃亏，事业却受益；二者，大多数老实人不吃亏，吃亏者是个别的；三者，即便个别人吃了亏，也是一时的，不会是长久的。归根结底，老实人不吃亏，不当老实人才会吃亏，而且往往吃大亏。在我们身边就有许多这样的例子，足以佐证。所以，我们应对“吃亏”一说有正确的认识，不要被错误的观点误导。提倡说老实话、办老实事、做老实人，领导干部应身体力行，做出榜样，这样才能带动群众形成风气。还有一点应该引起重视，就是建立鼓励说老实话、办老实事、做老实人的机制，使老实人真正“吃香”起来。如此，我们所倡导的说老实话、办老实事、做老实人才会落到实处，结出丰硕果实。 （作者单位：武警安徽省总队） 有一种忠诚叫坦诚 ■赵建雄 能不能讲真话、道实情，对于共产党员而言，是检验是否对党忠诚的重要方面。 天下至德，莫大于忠。忠诚作为党员干部的重要品质，突出反映在对党不欺瞒，以赤诚之心对待组织，以坦诚态度对待同志。坦诚之于忠诚，是真心实意的交流碰撞，是披肝沥胆的赤诚面对，是不计得失的患难与共，来不得半句假话谎言，容不得半点私心杂念。 坦诚不坦诚，群众看得清。说真话道实情，是内心世界的自然流淌，不需要费尽心机去修饰、绞尽脑汁去掩藏。仔细观察不难发现，真话是一箭中的、直击核心的简洁，让人一听就明白，一交流就共鸣。相反，如果心存杂念，就会用遮掩的语言来表达，让人听起来费劲，交流起来困难。群众的眼睛是雪亮的。假忠诚、伪忠诚，能蒙蔽人一时，但在群众面前，很快就会露出狐狸的尾巴。其实，说真话最容易，“巧诈不如拙诚”。须知，一句谎话，需要用十句假话去堵漏，而且，谎言只能骗人一次，不信任却会伴其一生。 言为心声，行为言形。忠诚，表态容易，可以誓言铿锵；但衡量忠诚，则需“听其言，观其行”，更应看重的是行动，是实打实的表里如一、磊落坦荡。坦诚不坦诚，平时有踪影，真实流露的心声和刻意表达的言行，是完全不同的两种境界。心中坦荡荡，行为亮堂堂。同样，心中有杂念，动作就变形。比如，有的人为了进步不惜年龄造假，但面对“大师”却生怕报错生辰八字；有的人正式场合讲马列、私下场合犯自由主义，要求别人守规矩、自己却在败坏风气；有的人隐瞒个人事项，不如实申报；有的人对自身缺点错误遮遮掩掩，上推下卸，强调客观，等等。明里一套、暗地一套，人前一套、背后一套，嘴上一套、行为一套，这不就是“两面人”吗？有人总结得好，真小人可恨，伪君子则可怕。 光明磊落、开诚布公，既是对他人的尊重、对组织的忠诚，也是对自己的负责。分析不坦诚的原因，不外乎因为有缺点甚或有污点，因而产生顾虑甚至惧怕心理。常识告诉我们，敞开心扉，春雨润泽；关闭心门，病菌滋生。对问题的态度比问题本身更重要，越捂越被动，最后只能自食苦果。要认识到，组织是坚强的后盾、有力的靠山，无话不可对党讲。把自己的一切毫无保留地“晒”在组织面前，好比在阳光雨露下除菌固根，能使自己放下包袱，重整行装，轻松前行。 作为一名共产党员，不忘初心，就要始终牢记入党时是怎么讲的；砥砺前行，就要时刻校正现在的思想言行。做到对党忠诚老实，就要光明磊落，坦诚坦荡，用自己的实际行动诠释什么叫对党忠诚、永不叛党。 （作者单位：国防大学联合指挥与参谋学院）";
				;
				Result r = nbm.classify(text);
		System.out.println(r.toString());
	}
	
	public static void test(NaiveBayesModel nbm, String dir) {
		File seeds = new File(dir);
		int all = 0;
		int right = 0;
		for(String seed : seeds.list()) {
			String newDir = dir + File.separator + seed;
			File s = new File(newDir);
			for(String textName : s.list()) {
				String text = FileReaderUtil.readFile(newDir + File.separator + textName);
				Result r = nbm.classify(text);
				System.out.println(seed + "\t" + r.toString());
				all++;
				if( seed.equals(r.getTag())) {
					right++;
				}
			}
		}
		System.out.println("all:\t" + all);
		System.out.println("right:\t" + right);
	}
	
	public static void testNew(NaiveBayesModel nbm, String dir) {
		int all = 0;
		int right = 0;
		int none = 0;
		for(String seed : new File(dir).list()) {
			String newDir = dir + File.separator + seed;
			for(String textName : new File(newDir).list()) {
				String text = readFile(newDir + File.separator + textName);
				Result r = nbm.classify(text);
				all++;
				if(r == null) {
					none++;
					continue;
				} else {
					System.out.println(seed + "\t" + r.toString());
				}
				if( seed.equals(r.getTag())) {
					right++;
				}
			}
		}
		System.out.println("all:\t" + all);
		System.out.println("right:\t" + right);
		System.out.println("none:\t" + none);
	}
	
	public static String readFile(String filePath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			StringBuilder text = new StringBuilder();
			String line = null;
			while((line = br.readLine()) != null) {
				text.append(line);
			}
			return text.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
