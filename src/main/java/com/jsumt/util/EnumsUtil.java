package com.jsumt.util;

/**
 * 枚举类集合
 * ClassName: Enums <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2017年6月14日 下午1:54:19 <br/>
 *
 * @author wyf
 * @version
 * @since JDK 1.6
 */
public class EnumsUtil
{
    /**
     * iconClass枚举类
     * ClassName: IconClass <br/>
     * Function: TODO ADD FUNCTION. <br/>
     * Reason: TODO ADD REASON(可选). <br/>
     * date: 2018年8月16日 下午1:48:35 <br/>
     *
     * @author wyf
     * @version EnumsUtil
     * @since JDK 1.6
     */
    public enum IconClass
    {
        ICON_EMPTY("icon-empty"), ICON_ORG("icon-org"), ICON_MENU("icon-menu"), ICON_ROLEGROUP("icon-roleGroup"),
        ICON_MODULE("icon-module"), ICON_USER("icon-user"), ICON_PROJECT("icon-project"), ICON_YHTYPE("icon-yhType");
        // 成员变量
        private String name;

        // 构造方法
        private IconClass(String name)
        {
            this.name = name;
        }

        @Override
        public String toString()
        {
            return this.name;
        }
    }
    public static void main(String[] args)
    {
        System.out.println(EnumsUtil.IconClass.ICON_MENU);
    }

}
