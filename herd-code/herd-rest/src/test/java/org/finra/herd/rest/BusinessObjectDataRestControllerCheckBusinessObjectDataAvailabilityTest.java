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
package org.finra.herd.rest;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.finra.herd.model.api.xml.BusinessObjectDataAvailability;
import org.finra.herd.model.api.xml.BusinessObjectDataAvailabilityRequest;
import org.finra.herd.model.api.xml.BusinessObjectDataStatus;
import org.finra.herd.model.jpa.BusinessObjectDataEntity;
import org.finra.herd.model.jpa.BusinessObjectDataStatusEntity;
import org.finra.herd.service.impl.BusinessObjectDataServiceImpl;

/**
 * This class tests checkBusinessObjectDataAvailability functionality within the business object data REST controller.
 */
public class BusinessObjectDataRestControllerCheckBusinessObjectDataAvailabilityTest extends AbstractRestTest
{
    @Test
    public void testCheckBusinessObjectDataAvailabilityPartitionValueList()
    {
        // Prepare test data and execute the check business object data availability request.
        businessObjectDataAvailabilityTestHelper.createDatabaseEntitiesForBusinessObjectDataAvailabilityTesting(null);
        BusinessObjectDataAvailabilityRequest request =
            businessObjectDataServiceTestHelper.getTestBusinessObjectDataAvailabilityRequest(UNSORTED_PARTITION_VALUES);
        BusinessObjectDataAvailability resultAvailability = businessObjectDataRestController.checkBusinessObjectDataAvailability(request);

        // Validate the results.
        List<BusinessObjectDataStatus> expectedAvailableStatuses = businessObjectDataServiceTestHelper
            .getTestBusinessObjectDataStatuses(FORMAT_VERSION, BusinessObjectDataEntity.FIRST_PARTITION_COLUMN_POSITION, STORAGE_1_AVAILABLE_PARTITION_VALUES,
                NO_SUBPARTITION_VALUES, DATA_VERSION, BusinessObjectDataStatusEntity.VALID, false);
        List<BusinessObjectDataStatus> expectedNotAvailableStatuses = businessObjectDataServiceTestHelper
            .getTestBusinessObjectDataStatuses(FORMAT_VERSION, BusinessObjectDataEntity.FIRST_PARTITION_COLUMN_POSITION,
                STORAGE_1_NOT_AVAILABLE_PARTITION_VALUES, null, DATA_VERSION, BusinessObjectDataServiceImpl.REASON_NOT_REGISTERED, false);
        businessObjectDataServiceTestHelper
            .validateBusinessObjectDataAvailability(request, expectedAvailableStatuses, expectedNotAvailableStatuses, resultAvailability);
    }

    @Test
    public void testCheckBusinessObjectDataAvailabilityPartitionValueListStandalonePartitionValueFilter()
    {
        // Prepare test data and execute the check business object data availability request with a standalone partition value filter.
        businessObjectDataAvailabilityTestHelper.createDatabaseEntitiesForBusinessObjectDataAvailabilityTesting(null);
        BusinessObjectDataAvailabilityRequest request =
            businessObjectDataServiceTestHelper.getTestBusinessObjectDataAvailabilityRequest(UNSORTED_PARTITION_VALUES);
        request.setPartitionValueFilter(request.getPartitionValueFilters().get(0));
        request.setPartitionValueFilters(null);
        BusinessObjectDataAvailability resultAvailability = businessObjectDataRestController.checkBusinessObjectDataAvailability(request);

        // Validate the results.
        List<BusinessObjectDataStatus> expectedAvailableStatuses = businessObjectDataServiceTestHelper
            .getTestBusinessObjectDataStatuses(FORMAT_VERSION, BusinessObjectDataEntity.FIRST_PARTITION_COLUMN_POSITION, STORAGE_1_AVAILABLE_PARTITION_VALUES,
                NO_SUBPARTITION_VALUES, DATA_VERSION, BusinessObjectDataStatusEntity.VALID, false);
        List<BusinessObjectDataStatus> expectedNotAvailableStatuses = businessObjectDataServiceTestHelper
            .getTestBusinessObjectDataStatuses(FORMAT_VERSION, BusinessObjectDataEntity.FIRST_PARTITION_COLUMN_POSITION,
                STORAGE_1_NOT_AVAILABLE_PARTITION_VALUES, null, DATA_VERSION, BusinessObjectDataServiceImpl.REASON_NOT_REGISTERED, true);
        businessObjectDataServiceTestHelper
            .validateBusinessObjectDataAvailability(request, expectedAvailableStatuses, expectedNotAvailableStatuses, resultAvailability);
    }

    @Test
    public void testCheckBusinessObjectDataAvailabilityPartitionValueRange()
    {
        // Prepare test data.
        businessObjectDataAvailabilityTestHelper.createDatabaseEntitiesForBusinessObjectDataAvailabilityTesting(PARTITION_KEY_GROUP);
        expectedPartitionValueDaoTestHelper.createExpectedPartitionValueProcessDatesForApril2014(PARTITION_KEY_GROUP);

        BusinessObjectDataAvailabilityRequest request;
        BusinessObjectDataAvailability resultAvailability;
        List<BusinessObjectDataStatus> expectedAvailableStatuses;
        List<BusinessObjectDataStatus> expectedNotAvailableStatuses;

        // Execute the check business object data availability request when start partition value is less than the end partition value.
        request = businessObjectDataServiceTestHelper.getTestBusinessObjectDataAvailabilityRequest(START_PARTITION_VALUE, END_PARTITION_VALUE);
        resultAvailability = businessObjectDataRestController.checkBusinessObjectDataAvailability(request);

        // Validate the results.
        expectedAvailableStatuses = businessObjectDataServiceTestHelper
            .getTestBusinessObjectDataStatuses(FORMAT_VERSION, BusinessObjectDataEntity.FIRST_PARTITION_COLUMN_POSITION,
                PROCESS_DATE_AVAILABLE_PARTITION_VALUES, NO_SUBPARTITION_VALUES, DATA_VERSION, BusinessObjectDataStatusEntity.VALID, false);
        expectedNotAvailableStatuses = businessObjectDataServiceTestHelper
            .getTestBusinessObjectDataStatuses(FORMAT_VERSION, BusinessObjectDataEntity.FIRST_PARTITION_COLUMN_POSITION,
                PROCESS_DATE_NOT_AVAILABLE_PARTITION_VALUES, null, DATA_VERSION, BusinessObjectDataServiceImpl.REASON_NOT_REGISTERED, false);
        businessObjectDataServiceTestHelper
            .validateBusinessObjectDataAvailability(request, expectedAvailableStatuses, expectedNotAvailableStatuses, resultAvailability);

        // Execute the check business object data availability request when start partition value is equal to the end partition value.
        request = businessObjectDataServiceTestHelper.getTestBusinessObjectDataAvailabilityRequest(START_PARTITION_VALUE, START_PARTITION_VALUE);
        resultAvailability = businessObjectDataRestController.checkBusinessObjectDataAvailability(request);

        // Validate the results.
        expectedAvailableStatuses = businessObjectDataServiceTestHelper
            .getTestBusinessObjectDataStatuses(FORMAT_VERSION, BusinessObjectDataEntity.FIRST_PARTITION_COLUMN_POSITION, Arrays.asList(START_PARTITION_VALUE),
                NO_SUBPARTITION_VALUES, DATA_VERSION, BusinessObjectDataStatusEntity.VALID, false);
        expectedNotAvailableStatuses = businessObjectDataServiceTestHelper
            .getTestBusinessObjectDataStatuses(FORMAT_VERSION, BusinessObjectDataEntity.FIRST_PARTITION_COLUMN_POSITION, null, null, DATA_VERSION,
                BusinessObjectDataServiceImpl.REASON_NOT_REGISTERED, false);
        businessObjectDataServiceTestHelper
            .validateBusinessObjectDataAvailability(request, expectedAvailableStatuses, expectedNotAvailableStatuses, resultAvailability);
    }
}
