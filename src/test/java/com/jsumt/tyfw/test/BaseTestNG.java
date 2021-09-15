package com.jsumt.tyfw.test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeSuite;

@ContextConfiguration( locations = {"classpath:applicationContext.xml"} )
public class BaseTestNG extends AbstractTestNGSpringContextTests
{
    @BeforeSuite( alwaysRun = true )
    public void init()
    {
        System.out.println("TestNg开始测试....");
    }
}
