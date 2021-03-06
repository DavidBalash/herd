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
package org.finra.herd.dao;

import org.junit.Test;

import org.finra.herd.model.dto.AwsParamsDto;

/**
 * This class tests the functionality of SqsDao.
 */
public class SqsDaoTest extends AbstractDaoTest
{
    @Test
    public void testSendSqsTextMessage() throws Exception
    {
        // Send a text message to the specified AWS SQS queue.
        // There is nothing to assert since not having an exception thrown is sufficient.
        AwsParamsDto testAwsParamsDto = new AwsParamsDto();
        testAwsParamsDto.setHttpProxyHost(HTTP_PROXY_HOST);
        testAwsParamsDto.setHttpProxyPort(HTTP_PROXY_PORT);
        sqsDao.sendSqsTextMessage(testAwsParamsDto, JMS_QUEUE_NAME, MESSAGE_TEXT);

        // Send a text message to the specified AWS SQS queue without specifying HTTP proxy settings.
        testAwsParamsDto.setHttpProxyHost(null);
        testAwsParamsDto.setHttpProxyPort(null);
        sqsDao.sendSqsTextMessage(testAwsParamsDto, JMS_QUEUE_NAME, MESSAGE_TEXT);
    }
}
