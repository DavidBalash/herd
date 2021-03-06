/*
* Copyright 2015 herd contributors
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.finra.herd.service.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.finra.herd.dao.impl.MockEc2OperationsImpl;
import org.finra.herd.model.api.xml.EmrClusterDefinition;
import org.finra.herd.model.api.xml.InstanceDefinition;
import org.finra.herd.model.api.xml.InstanceDefinitions;
import org.finra.herd.model.api.xml.MasterInstanceDefinition;
import org.finra.herd.model.api.xml.NodeTag;
import org.finra.herd.service.AbstractServiceTest;

/**
 * This class tests functionality within the EmrClusterDefinitionHelper class.
 */
public class EmrClusterDefinitionHelperTest extends AbstractServiceTest
{
    @Test
    public void testValidateEmrClusterDefinitionConfigurationNullSubnet()
    {
        EmrClusterDefinition emrClusterDefinition = createValidEmrClusterDefinition();
        emrClusterDefinition.setSubnetId(null);
        try
        {
            emrClusterDefinitionHelper.validateEmrClusterDefinitionConfiguration(emrClusterDefinition);
            fail("expected IllegalArgumentException, but no exception was thrown");
        }
        catch (Exception e)
        {
            assertEquals("thrown exception", IllegalArgumentException.class, e.getClass());
        }
    }

    /**
     * Tests case where validation is run against the definition generated by createValidEmrClusterDefinition() without modifications. The definition should be
     * valid.
     */
    @Test
    public void testValidateEmrClusterDefinitionConfigurationValid()
    {
        EmrClusterDefinition emrClusterDefinition = createValidEmrClusterDefinition();

        try
        {
            emrClusterDefinitionHelper.validateEmrClusterDefinitionConfiguration(emrClusterDefinition);
        }
        catch (Exception e)
        {
            fail("expected no exception, but " + e.getClass() + " was thrown. " + e);
        }
    }

    /**
     * Tests case where validation is run against the definition generated by createValidEmrClusterDefinition() The master instance spot price is specified The
     * definition should be valid because spot price is allowed when max search price is not specified.
     */
    @Test
    public void testValidateEmrClusterDefinitionConfigurationMasterSpotPriceSpecified()
    {
        EmrClusterDefinition emrClusterDefinition = createValidEmrClusterDefinition();
        emrClusterDefinition.getInstanceDefinitions().getMasterInstances().setInstanceSpotPrice(BigDecimal.ONE);

        try
        {
            emrClusterDefinitionHelper.validateEmrClusterDefinitionConfiguration(emrClusterDefinition);
        }
        catch (Exception e)
        {
            fail("expected no exception, but " + e.getClass() + " was thrown. " + e);
        }
    }

    /**
     * Tests case where validation is run against the definition generated by createValidEmrClusterDefinition() The master instance max search price is
     * specified The definition should be valid because max search price is allowed when no instance spot price is specified.
     */
    @Test
    public void testValidateEmrClusterDefinitionConfigurationMasterMaxSearchPriceSpecified()
    {
        EmrClusterDefinition emrClusterDefinition = createValidEmrClusterDefinition();
        emrClusterDefinition.getInstanceDefinitions().getMasterInstances().setInstanceMaxSearchPrice(BigDecimal.ONE);

        try
        {
            emrClusterDefinitionHelper.validateEmrClusterDefinitionConfiguration(emrClusterDefinition);
        }
        catch (Exception e)
        {
            fail("expected no exception, but " + e.getClass() + " was thrown. " + e);
        }
    }

    /**
     * Tests case where validation is run against the definition generated by createValidEmrClusterDefinition() The master instance max search price and
     * on-demand threshold is specified The definition should be valid because on-demand threshold can be used with max search price.
     */
    @Test
    public void testValidateEmrClusterDefinitionConfigurationMasterMaxSearchPriceAndOnDemandThresholdSpecified()
    {
        EmrClusterDefinition emrClusterDefinition = createValidEmrClusterDefinition();
        emrClusterDefinition.getInstanceDefinitions().getMasterInstances().setInstanceMaxSearchPrice(BigDecimal.ONE);
        emrClusterDefinition.getInstanceDefinitions().getMasterInstances().setInstanceOnDemandThreshold(BigDecimal.ONE);

        try
        {
            emrClusterDefinitionHelper.validateEmrClusterDefinitionConfiguration(emrClusterDefinition);
        }
        catch (Exception e)
        {
            fail("expected no exception, but " + e.getClass() + " was thrown. " + e);
        }
    }

    /**
     * Tests case where validation is run against the definition generated by createValidEmrClusterDefinition() The task instance is not specified. The
     * definition should be valid because task instance is optional.
     */
    @Test
    public void testValidateEmrClusterDefinitionConfigurationTaskInstanceDefinitionNotSpecified()
    {
        EmrClusterDefinition emrClusterDefinition = createValidEmrClusterDefinition();
        emrClusterDefinition.getInstanceDefinitions().setTaskInstances(null);

        try
        {
            emrClusterDefinitionHelper.validateEmrClusterDefinitionConfiguration(emrClusterDefinition);
        }
        catch (Exception e)
        {
            fail("expected no exception, but " + e.getClass() + " was thrown. " + e);
        }
    }

    /**
     * Tests case where validation is run against the definition generated by createValidEmrClusterDefinition() The subnet is whitespace only. The definition is
     * not valid. Subnet is required.
     */
    @Test
    public void testValidateEmrClusterDefinitionConfigurationBlankSubnet()
    {
        EmrClusterDefinition emrClusterDefinition = createValidEmrClusterDefinition();
        emrClusterDefinition.setSubnetId(" \r\t\n");
        try
        {
            emrClusterDefinitionHelper.validateEmrClusterDefinitionConfiguration(emrClusterDefinition);
            fail("expected IllegalArgumentException, but no exception was thrown");
        }
        catch (Exception e)
        {
            assertEquals("thrown exception", IllegalArgumentException.class, e.getClass());
        }
    }

    /**
     * Tests case where validation is run against the definition generated by createValidEmrClusterDefinition() The subnet is a list, and contains at least 1
     * whitespace-only element The definition is not valid. All elements in subnet list must not be blank.
     */
    @Test
    public void testValidateEmrClusterDefinitionConfigurationSubnetListBlankElement()
    {
        EmrClusterDefinition emrClusterDefinition = createValidEmrClusterDefinition();
        emrClusterDefinition.setSubnetId(MockEc2OperationsImpl.SUBNET_1 + ", \r\t\n");
        try
        {
            emrClusterDefinitionHelper.validateEmrClusterDefinitionConfiguration(emrClusterDefinition);
            fail("expected IllegalArgumentException, but no exception was thrown");
        }
        catch (Exception e)
        {
            assertEquals("thrown exception", IllegalArgumentException.class, e.getClass());
        }
    }

    /**
     * Tests case where validation is run against the definition generated by createValidEmrClusterDefinition() The master spot price is negative. The
     * definition is not valid. All prices must be positive.
     */
    @Test
    public void testValidateEmrClusterDefinitionConfigurationMasterSpotPriceNegative()
    {
        EmrClusterDefinition emrClusterDefinition = createValidEmrClusterDefinition();
        emrClusterDefinition.getInstanceDefinitions().getMasterInstances().setInstanceSpotPrice(BigDecimal.ONE.negate());

        try
        {
            emrClusterDefinitionHelper.validateEmrClusterDefinitionConfiguration(emrClusterDefinition);
            fail("expected IllegalArgumentException, but no exception was thrown");
        }
        catch (Exception e)
        {
            assertEquals("thrown exception", IllegalArgumentException.class, e.getClass());
        }
    }

    /**
     * Tests case where validation is run against the definition generated by createValidEmrClusterDefinition() The master instance spot price and max search
     * price is specified. The definition is not valid. The two parameters are exclusive.
     */
    @Test
    public void testValidateEmrClusterDefinitionConfigurationMasterSpotPriceAndMaxSearchPriceSpecified()
    {
        EmrClusterDefinition emrClusterDefinition = createValidEmrClusterDefinition();
        emrClusterDefinition.getInstanceDefinitions().getMasterInstances().setInstanceSpotPrice(BigDecimal.ONE);
        emrClusterDefinition.getInstanceDefinitions().getMasterInstances().setInstanceMaxSearchPrice(BigDecimal.ONE);

        try
        {
            emrClusterDefinitionHelper.validateEmrClusterDefinitionConfiguration(emrClusterDefinition);
            fail("expected IllegalArgumentException, but no exception was thrown");
        }
        catch (Exception e)
        {
            assertEquals("thrown exception", IllegalArgumentException.class, e.getClass());
        }
    }

    /**
     * Tests case where validation is run against the definition generated by createValidEmrClusterDefinition() The master instance max search price is
     * negative. The definition is not valid. All prices must be positive.
     */
    @Test
    public void testValidateEmrClusterDefinitionConfigurationMasterMaxSearchPriceNegative()
    {
        EmrClusterDefinition emrClusterDefinition = createValidEmrClusterDefinition();
        emrClusterDefinition.getInstanceDefinitions().getMasterInstances().setInstanceMaxSearchPrice(BigDecimal.ONE.negate());

        try
        {
            emrClusterDefinitionHelper.validateEmrClusterDefinitionConfiguration(emrClusterDefinition);
            fail("expected IllegalArgumentException, but no exception was thrown");
        }
        catch (Exception e)
        {
            assertEquals("thrown exception", IllegalArgumentException.class, e.getClass());
        }
    }

    /**
     * Tests case where validation is run against the definition generated by createValidEmrClusterDefinition() The master instance on-demand threshold is
     * specified. The definition is not valid. On-demand threshold is only allowed when max search price is specified.
     */
    @Test
    public void testValidateEmrClusterDefinitionConfigurationMasterOnDemandThresholdSpecified()
    {
        EmrClusterDefinition emrClusterDefinition = createValidEmrClusterDefinition();
        emrClusterDefinition.getInstanceDefinitions().getMasterInstances().setInstanceOnDemandThreshold(BigDecimal.ONE);

        try
        {
            emrClusterDefinitionHelper.validateEmrClusterDefinitionConfiguration(emrClusterDefinition);
            fail("expected IllegalArgumentException, but no exception was thrown");
        }
        catch (Exception e)
        {
            assertEquals("thrown exception", IllegalArgumentException.class, e.getClass());
        }
    }

    /**
     * Tests case where validation is run against the definition generated by createValidEmrClusterDefinition() The master instance on-demand threshold is
     * negative. The definition is not valid. All prices must be positive.
     */
    @Test
    public void testValidateEmrClusterDefinitionConfigurationMasterMaxSearchPriceSpecifiedAndOnDemandThresholdNegative()
    {
        EmrClusterDefinition emrClusterDefinition = createValidEmrClusterDefinition();
        emrClusterDefinition.getInstanceDefinitions().getMasterInstances().setInstanceMaxSearchPrice(BigDecimal.ONE);
        emrClusterDefinition.getInstanceDefinitions().getMasterInstances().setInstanceOnDemandThreshold(BigDecimal.ONE.negate());

        try
        {
            emrClusterDefinitionHelper.validateEmrClusterDefinitionConfiguration(emrClusterDefinition);
            fail("expected IllegalArgumentException, but no exception was thrown");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("Master instance on-demand threshold must be greater than 0", e.getMessage());
        }
    }

    @Test
    public void testValidateEmrClusterDefinitionConfigurationCoreInstancesNotSpecified()
    {
        EmrClusterDefinition emrClusterDefinition = createValidEmrClusterDefinition();
        emrClusterDefinition.getInstanceDefinitions().setCoreInstances(null);

        try
        {
            emrClusterDefinitionHelper.validateEmrClusterDefinitionConfiguration(emrClusterDefinition);
        }
        catch (Exception e)
        {
            fail("expected no exception, but " + e.getClass() + " was thrown. " + e);
        }
    }

    @Test
    public void testValidateEmrClusterDefinitionConfigurationCoreInstanceSpecifiedInstanceCountNegative()
    {
        EmrClusterDefinition emrClusterDefinition = createValidEmrClusterDefinition();
        emrClusterDefinition.getInstanceDefinitions().getCoreInstances().setInstanceCount(-1);

        try
        {
            emrClusterDefinitionHelper.validateEmrClusterDefinitionConfiguration(emrClusterDefinition);
            fail();
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("At least 0 core instance must be specified.", e.getMessage());
        }
    }

    /**
     * Creates a EMR cluster definition which does not cause validateEmrClusterDefinitionConfiguration() to throw an exception.
     * <p/>
     * - One subnet is specified - Master, core, and task instances are specified - Instance count, and instance type are specified for each instance
     * definition. - One node tag is specified
     *
     * @return A new instance of {@link EmrClusterDefinition}
     */
    private EmrClusterDefinition createValidEmrClusterDefinition()
    {
        EmrClusterDefinition emrClusterDefinition = new EmrClusterDefinition();
        emrClusterDefinition.setSubnetId(MockEc2OperationsImpl.SUBNET_1);
        InstanceDefinitions instanceDefinitions = new InstanceDefinitions();

        MasterInstanceDefinition masterInstanceDefinition = new MasterInstanceDefinition();
        masterInstanceDefinition.setInstanceCount(1);
        masterInstanceDefinition.setInstanceType(MockEc2OperationsImpl.INSTANCE_TYPE_1);
        instanceDefinitions.setMasterInstances(masterInstanceDefinition);

        InstanceDefinition coreInstanceDefinition = new InstanceDefinition();
        coreInstanceDefinition.setInstanceCount(1);
        coreInstanceDefinition.setInstanceType(MockEc2OperationsImpl.INSTANCE_TYPE_1);
        instanceDefinitions.setCoreInstances(coreInstanceDefinition);

        InstanceDefinition taskInstanceDefinition = new InstanceDefinition();
        taskInstanceDefinition.setInstanceCount(1);
        taskInstanceDefinition.setInstanceType(MockEc2OperationsImpl.INSTANCE_TYPE_1);
        instanceDefinitions.setTaskInstances(taskInstanceDefinition);

        emrClusterDefinition.setInstanceDefinitions(instanceDefinitions);

        List<NodeTag> nodeTags = new ArrayList<>();
        {
            nodeTags.add(new NodeTag("test_nodeTagName", "test_nodeTagValue"));
        }
        emrClusterDefinition.setNodeTags(nodeTags);

        return emrClusterDefinition;
    }
}
