package cn.chinesetv.app;

import java.util.HashMap;
import java.util.Map;

final class ChannelNameTranslator {
    private static final Map<String, String> NAMES = new HashMap<String, String>();

    static {
        add("ABNChina.us", "ABN中国台");
        add("AndoTV.cn", "安多卫视");
        add("AngelTV.in", "天使电视中文台");
        add("AnhuiTV.cn", "安徽卫视");
        add("AnshunComprehensiveNewsChannel.cn", "安顺新闻综合频道");
        add("BreadTV.cn", "面包台");
        add("BRTVKakuChildrensChannel.cn", "北京卡酷少儿频道");
        add("BeijingSatelliteTV.cn", "北京卫视");
        add("CCTVPlus1.cn", "CCTV+ 1 新闻");
        add("CCTVPlus2.cn", "CCTV+ 2 新闻");
        add("CCTV1.cn", "CCTV-1 综合");
        add("CCTV2.cn", "CCTV-2 财经");
        add("CCTV3.cn", "CCTV-3 综艺");
        add("CCTV4K.cn", "CCTV-4K 超高清");
        add("CCTV5Plus.cn", "CCTV-5+ 体育赛事");
        add("CCTV6.cn", "CCTV-6 电影");
        add("CCTV7.cn", "CCTV-7 国防军事");
        add("CCTV8.cn", "CCTV-8 电视剧");
        add("CCTV8K.cn", "CCTV-8K 超高清");
        add("CCTV9.cn", "CCTV-9 纪录");
        add("CCTV10.cn", "CCTV-10 科教");
        add("CCTV11.cn", "CCTV-11 戏曲");
        add("CCTV12.cn", "CCTV-12 社会与法");
        add("CCTV13.cn", "CCTV-13 新闻");
        add("CCTV14.cn", "CCTV-14 少儿");
        add("CCTV15.cn", "CCTV-15 音乐");
        add("CCTV16.cn", "CCTV-16 奥林匹克");
        add("CCTV17.cn", "CCTV-17 农业农村");
        add("CCTVBilliards.cn", "CCTV 台球");
        add("CCTVCultureofQuality.cn", "CCTV 央视文化精品");
        add("CCTVGolfTennis.cn", "CCTV 高尔夫网球");
        add("CCTVHealth.cn", "CCTV 卫生健康");
        add("CCTVNostalgiaTheater.cn", "CCTV 怀旧剧场");
        add("CCTVStormFootball.cn", "CCTV 风云足球");
        add("CCTVStormMusic.cn", "CCTV 风云音乐");
        add("CCTVStormTheater.cn", "CCTV 风云剧场");
        add("CCTVTheFirstTheater.cn", "CCTV 第一剧场");
        add("CCTVWeaponTechnology.cn", "CCTV 兵器科技");
        add("CCTVWomensFashion.cn", "CCTV 女性时尚");
        add("CCTVWorldGeography.cn", "CCTV 世界地理");
        add("CETV1.cn", "中国教育电视台一套");
        add("CETV2.cn", "中国教育电视台二套");
        add("ChifengComprehensiveNewsChanel.cn", "赤峰新闻综合频道");
        add("ChuxiongNewsChannel.cn", "楚雄新闻频道");
        add("CNDFilmMiddleSchoolChannel.cn", "中学生频道");
        add("DragonTVInternational.cn", "东方卫视国际频道");
        add("FujianComprehensiveChannel.cn", "福建综合频道");
        add("GuangdongSatelliteTV.cn", "广东卫视");
        add("GuangxiVarietyTravelChannel.cn", "广西综艺旅游频道");
        add("GuangzhouTV.cn", "广州综合频道");
        add("HarbinComprehensiveNewsChannel.cn", "哈尔滨新闻综合频道");
        add("HarbinMovieChannel.cn", "哈尔滨影视频道");
        add("HebeiTV.cn", "河北卫视");
        add("HomePlus.ir", "家庭影院台");
        add("HunanTV.cn", "湖南卫视");
        add("JiangxiChildrensChannel.cn", "江西少儿频道");
        add("JiangxiCityChannel.cn", "江西都市频道");
        add("JiangxiEconomyLifeChannel.cn", "江西经济生活频道");
        add("JiangxiMovieChannel.cn", "江西影视频道");
        add("JiangxiPublicAgricultureChannel.cn", "江西公共农业频道");
        add("JilinCityChannel.cn", "吉林都市频道");
        add("JilinLifestyleChannel.cn", "吉林生活频道");
        add("JilinMovieChannel.cn", "吉林影视频道");
        add("JilinRuralChannel.cn", "吉林乡村频道");
        add("KangbaTV.cn", "康巴卫视");
        add("LanzhouComprehensiveNewsChannel.cn", "兰州新闻综合频道");
        add("LanzhouCultureTourismChannel.cn", "兰州文化旅游频道");
        add("LiangshanTV.cn", "凉山电视台");
        add("NanchangNewsGeneralistChannel.cn", "南昌新闻综合频道");
        add("NeiMonggolTV.cn", "内蒙古卫视");
        add("NeiMonggolTV2MongolianCultureChannel.cn", "内蒙古蒙古语文化频道");
        add("QTV1.cn", "青岛新闻综合频道");
        add("QTV2.cn", "青岛生活服务频道");
        add("QTV3.cn", "青岛影视频道");
        add("QTV4.cn", "青岛财经资讯频道");
        add("QTV5.cn", "青岛都市频道");
        add("QTV6.cn", "青岛教育频道");
        add("ShenzhenSatelliteTV.cn", "深圳卫视");
        add("SipingTV.cn", "四平电视台");
        add("TonghuaTV.cn", "通化电视台");
        add("TVBRICSChinese.cn", "金砖电视中文台");
        add("VoATVChina.cn", "美国之音中文台");
        add("XinjiangTV1.cn", "新疆电视台一套");
        add("XinjiangTV2.cn", "新疆电视台二套");
        add("XinjiangTV3.cn", "新疆电视台三套");
        add("XinjiangTV8.cn", "新疆电视台八套");
        add("XizangTVTibetan.cn", "西藏卫视藏语频道");
        add("ZhejiangInternationalChannel.cn", "浙江国际频道");
        add("ChinaWeatherChannel.cn", "中国气象频道");
        add("YunnanSatelliteTV.cn", "云南卫视");
        add("YouManCartoonChannel.cn", "优漫卡通");
    }

    private ChannelNameTranslator() {
    }

    static String translate(String tvgId, String originalName) {
        String translated = null;
        if (tvgId != null) {
            int variantSeparator = tvgId.indexOf('@');
            String baseId = variantSeparator >= 0 ? tvgId.substring(0, variantSeparator) : tvgId;
            translated = NAMES.get(baseId);
        }
        if (translated == null) {
            translated = originalName;
        } else {
            int roundMetadata = originalName.indexOf(" (");
            int squareMetadata = originalName.indexOf(" [");
            int metadataStart = roundMetadata < 0 ? squareMetadata
                    : squareMetadata < 0 ? roundMetadata : Math.min(roundMetadata, squareMetadata);
            if (metadataStart >= 0) {
                translated += originalName.substring(metadataStart);
            }
        }
        return translated.replace("[Not 24/7]", "[非全天播出]")
                .replace("[Geo-blocked]", "[地区限制]");
    }

    private static void add(String id, String name) {
        NAMES.put(id, name);
    }
}
