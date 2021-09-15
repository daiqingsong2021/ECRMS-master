/**
 * Project Name:ECRMS
 * File Name:TypeBean.java
 * Package Name:com.jsumt.vo.safe
 * Date:2018年12月20日上午9:44:03
 * Copyright (c) 2018, wuyf5@asiainfo-linkage.com All Rights Reserved.
 *
*/

package com.jsumt.vo.safe;
/**安全生产投入类别表
 * ClassName:TypeBean <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2018年12月20日 上午9:44:03 <br/>
 * @author   zll
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class TypeBean 
{
    private String id;
    private String is_one;
    private String title;
    private String id_one;
    private String order_no;
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
    }
    public String getIs_one()
    {
        return is_one;
    }
    public void setIs_one(String is_one)
    {
        this.is_one = is_one;
    }
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public String getId_one()
    {
        return id_one;
    }
    public void setId_one(String id_one)
    {
        this.id_one = id_one;
    }
    public String getOrder_no()
    {
        return order_no;
    }
    public void setOrder_no(String order_no)
    {
        this.order_no = order_no;
    }
    
}

