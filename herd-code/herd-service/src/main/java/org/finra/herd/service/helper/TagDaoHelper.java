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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.finra.herd.dao.TagDao;
import org.finra.herd.model.AlreadyExistsException;
import org.finra.herd.model.ObjectNotFoundException;
import org.finra.herd.model.api.xml.TagKey;
import org.finra.herd.model.jpa.TagEntity;

@Component
public class TagDaoHelper
{
    @Autowired
    private TagDao tagDao;

    /**
     * Ensures that a tag entity does not exist for a specified tag type code and display name.
     *
     * @param tagCode the specified tag type code.
     * @param displayName the specified display name.
     */
    public void assertDisplayNameDoesNotExistForTag(String tagCode, String displayName)
    {
        TagEntity tagEntity = tagDao.getTagByTagTypeAndDisplayName(tagCode, displayName);

        if (tagEntity != null)
        {
            throw new AlreadyExistsException(String
                .format("Display name \"%s\" already exists for a tag with tag type \"%s\" and tag code \"%s\".", displayName, tagEntity.getTagType().getCode(),
                    tagEntity.getTagCode()));
        }
    }

    /**
     * Gets a tag entity and ensure it exists.
     *
     * @param tagKey the tag (case insensitive)
     *
     * @return the tag entity
     * @throws org.finra.herd.model.ObjectNotFoundException if the tag entity doesn't exist
     */
    public TagEntity getTagEntity(TagKey tagKey) throws ObjectNotFoundException
    {
        TagEntity tagEntity = tagDao.getTagByKey(tagKey);

        if (tagEntity == null)
        {
            throw new ObjectNotFoundException(
                String.format("Tag with code \"%s\" doesn't exist for tag type \"%s\".", tagKey.getTagCode(), tagKey.getTagTypeCode()));
        }

        return tagEntity;
    }
}
