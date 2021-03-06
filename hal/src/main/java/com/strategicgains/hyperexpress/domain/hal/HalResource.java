/*
    Copyright 2013, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package com.strategicgains.hyperexpress.domain.hal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.strategicgains.hyperexpress.domain.LinkDefinition;
import com.strategicgains.hyperexpress.domain.Resource;



/**
 * A HAL Resource instance, containing links and embedded resources.
 * 
 * @author toddf
 * @since May 21, 2013
 */
public class HalResource
implements Resource
{
	/**
	 * The reserved "_links" property is OPTIONAL.
	 * 
	 * It is an object whose property names are link relation types (as
	 * defined by [RFC5988]) and values are either a Link Object or an array
	 * of Link Objects.
	 * 
	 * The relationship from the referencing object to the referenced object (e.g. rel=self).
	 * @see http://www.iana.org/assignments/link-relations/link-relations.xml
	 * 
	 * atom:link elements MAY have a "rel" attribute that indicates the link
	 * relation type.  If the "rel" attribute is not present, the link
	 * element MUST be interpreted as if the link relation type is "alternate".
	 * <p/>
	 * The value of "rel" MUST be a string that is non-empty and matches
	 * either the "isegment-nz-nc" or the "IRI" production in [RFC3987].
	 * <p/>
	 * Note that use of a relative reference other than a simple name is not
	 * allowed.  If a name is given, implementations MUST consider the link
	 * relation type equivalent to the same name registered within the IANA
	 */
	private Map<String, Object> _links;
	
	/**
	 * The reserved "_embedded" property is OPTIONAL
	 * 
	 * It is an object whose property names are link relation types (as
	 * defined by [RFC5988]) and values are either a Resource Object or an
	 * array of Resource Objects.
	 * 
	 * Embedded Resources MAY be a full, partial, or inconsistent version of
	 * the representation served from the target URI.
	 */
	private Map<String, Object> _embedded;

	@Override
	public void linkTo(String rel, LinkDefinition linkDefinition)
	{
		if (!hasLinks())
		{
			_links = new HashMap<String, Object>();
		}

		Object listOrLink = _links.get(rel);
		
		if (listOrLink == null)	// Add a single Link
		{
			_links.put(rel, new HalLink(linkDefinition));
		}
		else if (listOrLink.getClass().isAssignableFrom(ArrayList.class))	// Add Link to list.
		{
			((List<HalLink>) listOrLink).add(new HalLink(linkDefinition));
		}
		else // Convert to a list of Links
		{
			List<HalLink> list = new ArrayList<HalLink>();
			list.add((HalLink) listOrLink);
			list.add(new HalLink(linkDefinition));
			_links.put(rel, list);
		}
	}

	public boolean hasLinks()
	{
		return (_links != null);
	}

	public Map<String, Object> getLinks()
	{
		return Collections.unmodifiableMap(_links);
	}

	public void setLinks(Map<String, Object> links)
	{
		this._links = (links == null ? null : new HashMap<String, Object>(links));
	}

	public Map<String, Object> getEmbedded()
	{
		return Collections.unmodifiableMap(_embedded);
	}

	public void embed(String rel, Object resource)
	{
		Object listOrResource = acquireEmbedded(rel);
		
		if (listOrResource == null) // Add a single resource.
		{
			_embedded.put(rel, resource);
		}
		else if (listOrResource.getClass().isAssignableFrom(ArrayList.class))	// Add a resource to the list.
		{
			((List<Object>) listOrResource).add(resource);
		}
		else // Convert a single resource to a list of resources
		{
			List<Object> list = new ArrayList<Object>();
			list.add(listOrResource);
			list.add(resource);
			_embedded.put(rel, list);
		}
	}

	public void embed(String rel, Collection<? extends Object> resources)
	{
		Object listOrResource = acquireEmbedded(rel);
		
		if (listOrResource == null) // Create a new list.
		{
			_embedded.put(rel, new ArrayList<Object>(resources));
		}
		else if (listOrResource.getClass().isAssignableFrom(ArrayList.class))	// Add the resources to the list.
		{
			((List<Object>) listOrResource).addAll(resources);
		}
		else // Convert a single resource to a list of resources
		{
			List<Object> list = new ArrayList<Object>();
			list.add(listOrResource);
			list.addAll(resources);
			_embedded.put(rel, list);
		}
	}

	private Object acquireEmbedded(String rel)
    {
	    if (_embedded == null)
		{
			_embedded = new HashMap<String, Object>();
		}

		return _embedded.get(rel);
    }
}
