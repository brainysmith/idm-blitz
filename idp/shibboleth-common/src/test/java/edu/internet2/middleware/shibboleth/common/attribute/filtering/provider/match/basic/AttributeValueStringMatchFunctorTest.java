/*
 * Licensed to the University Corporation for Advanced Internet Development, 
 * Inc. (UCAID) under one or more contributor license agreements.  See the 
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache 
 * License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.internet2.middleware.shibboleth.common.attribute.filtering.provider.match.basic;

import edu.internet2.middleware.shibboleth.common.attribute.filtering.provider.FilterProcessingException;
import edu.internet2.middleware.shibboleth.common.attribute.filtering.provider.match.BaseTestCase;

/** {@link AttributeValueStringMatchFunctor} unit test. */
public class AttributeValueStringMatchFunctorTest extends BaseTestCase {

    /** {@inheritDoc} */
    public void setUp() throws Exception {
        super.setUp();
        AttributeValueStringMatchFunctor functor = new AttributeValueStringMatchFunctor();
        matchFunctor = functor;
        functor.setAttributeId(sAttribute.getId());
        functor.setMatchString(sAttribute.getValues().toArray(new String[] {})[0]);
    }

    public void testPermitValue() {
        try {
            assertTrue("evaluatePermitValue", matchFunctor.evaluatePermitValue(filterContext, null, "one"));
            assertTrue("evaluatePermitValue", matchFunctor.evaluatePermitValue(filterContext, null, "ONE"));
            ((AttributeValueStringMatchFunctor) matchFunctor).setCaseSensitive(true);
            assertFalse("evaluatePermitValue", matchFunctor.evaluatePermitValue(filterContext, null, "ONE"));
            assertFalse("evaluatePermitValue", matchFunctor.evaluatePermitValue(filterContext, null, "two"));
        } catch (FilterProcessingException e) {
            fail(e.getLocalizedMessage());
        }
    }

    public void testPolicyRequirement() {
        try {
            AttributeValueStringMatchFunctor functor = (AttributeValueStringMatchFunctor) matchFunctor;
            assertTrue("evaluatePolicyRequirement", matchFunctor.evaluatePolicyRequirement(filterContext));
            functor.setMatchString("three");
            assertFalse("evaluatePolicyRequirement", matchFunctor.evaluatePolicyRequirement(filterContext));
            sAttribute.getValues().add("two");
            assertFalse("evaluatePolicyRequirement", matchFunctor.evaluatePolicyRequirement(filterContext));
            sAttribute.getValues().add("three");
            assertTrue("evaluatePolicyRequirement", matchFunctor.evaluatePolicyRequirement(filterContext));
            functor.setAttributeId("wibble");
            assertFalse("evaluatePolicyRequirement", matchFunctor.evaluatePolicyRequirement(filterContext));

        } catch (FilterProcessingException e) {
            fail(e.getLocalizedMessage());
        }
    }
}
