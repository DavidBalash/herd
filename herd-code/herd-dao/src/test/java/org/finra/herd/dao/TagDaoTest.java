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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.finra.herd.model.api.xml.TagKey;
import org.finra.herd.model.jpa.TagEntity;
import org.finra.herd.model.jpa.TagTypeEntity;

public class TagDaoTest extends AbstractDaoTest
{
    @Test
    public void testGetTagByKey()
    {
        // Create a tag entity.
        TagEntity tagEntity = tagDaoTestHelper.createTagEntity(TAG_TYPE, TAG_CODE, TAG_DISPLAY_NAME, TAG_DESCRIPTION);

        // Get tag entity and validate.
        assertEquals(tagEntity, tagDao.getTagByKey(new TagKey(TAG_TYPE, TAG_CODE)));

        // Get tag entity by passing all case-insensitive parameters in uppercase.
        assertEquals(tagEntity, tagDao.getTagByKey(new TagKey(TAG_TYPE.toUpperCase(), TAG_CODE.toUpperCase())));

        // Get tag entity by passing all case-insensitive parameters in lowercase.
        assertEquals(tagEntity, tagDao.getTagByKey(new TagKey(TAG_TYPE.toLowerCase(), TAG_CODE.toLowerCase())));

        // Try invalid values for all input parameters.
        assertNull(tagDao.getTagByKey(new TagKey("I_DO_NOT_EXIST", TAG_CODE)));
        assertNull(tagDao.getTagByKey(new TagKey(TAG_TYPE, "I_DO_NOT_EXIST")));
    }

    @Test
    public void testGetTagByTagTypeAndDisplayName()
    {
        // Create a tag entity.
        TagEntity tagEntity = tagDaoTestHelper.createTagEntity(TAG_TYPE, TAG_CODE, TAG_DISPLAY_NAME, TAG_DESCRIPTION);

        // Get tag entity and validate.
        assertEquals(tagEntity, tagDao.getTagByTagTypeAndDisplayName(TAG_TYPE, TAG_DISPLAY_NAME));

        // Get tag entity by passing all case-insensitive parameters in uppercase.
        assertEquals(tagEntity, tagDao.getTagByTagTypeAndDisplayName(TAG_TYPE.toUpperCase(), TAG_DISPLAY_NAME.toUpperCase()));

        // Get tag entity by passing all case-insensitive parameters in lowercase.
        assertEquals(tagEntity, tagDao.getTagByTagTypeAndDisplayName(TAG_TYPE.toLowerCase(), TAG_DISPLAY_NAME.toLowerCase()));

        // Try invalid values for all input parameters.
        assertNull(tagDao.getTagByTagTypeAndDisplayName("I_DO_NOT_EXIST", TAG_DISPLAY_NAME));
        assertNull(tagDao.getTagByTagTypeAndDisplayName(TAG_TYPE, "I_DO_NOT_EXIST"));
    }

    @Test
    public void testGetTags()
    {
        // Create a list of tag key for the same tag type.
        List<TagKey> tagKeys = Arrays.asList(new TagKey(TAG_TYPE, TAG_CODE), new TagKey(TAG_TYPE, TAG_CODE_2));

        // Create a tag type entity.
        TagTypeEntity tagTypeEntity = tagTypeDaoTestHelper.createTagTypeEntity(TAG_TYPE, TAG_TYPE_DISPLAY_NAME, INTEGER_VALUE);

        // Create two tag entities.
        tagDaoTestHelper.createTagEntity(tagTypeEntity, TAG_CODE, TAG_DISPLAY_NAME, TAG_DESCRIPTION);
        tagDaoTestHelper.createTagEntity(tagTypeEntity, TAG_CODE_2, TAG_DISPLAY_NAME_2, TAG_DESCRIPTION_2);

        // Retrieve a list of tag keys.
        assertEquals(tagKeys, tagDao.getTagsByTagType(TAG_TYPE));

        // Get tag keys by passing all case-insensitive parameters in uppercase.
        assertEquals(tagKeys, tagDao.getTagsByTagType(TAG_TYPE.toUpperCase()));

        // Get tag keys by passing all case-insensitive parameters in lowercase.
        assertEquals(tagKeys, tagDao.getTagsByTagType(TAG_TYPE.toLowerCase()));

        // Try invalid values for all input parameters.
        assertTrue(tagDao.getTagsByTagType("I_DO_NOT_EXIST").isEmpty());
    }
}
