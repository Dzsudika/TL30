package com.adobe.summit.emea.core.models;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.apache.sling.models.annotations.*;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.Session;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kassa on 03/04/2019.
 */

@Model(
        // This must adapt from a SlingHttpServletRequest, since this is invoked directly via a request, and not via a resource.
        // If can specify Resource.class as a second adaptable as needed
        adaptables = { SlingHttpServletRequest.class },
        // The resourceType is required if you want Sling to "naturally" expose this model as the exporter for a Resource.
        resourceType = "aem-pwa-blog/components/content/tiles",
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
@Exporter(name = "jackson", extensions = "json", options = {
        /**
         * Jackson options:
         * - Mapper Features: http://static.javadoc.io/com.fasterxml.jackson.core/jackson-databind/2.8.5/com/fasterxml/jackson/databind/MapperFeature.html
         * - Serialization Features: http://static.javadoc.io/com.fasterxml.jackson.core/jackson-databind/2.8.5/com/fasterxml/jackson/databind/SerializationFeature.html
         */
        @ExporterOption(name = "MapperFeature.SORT_PROPERTIES_ALPHABETICALLY", value = "true"),
        @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", value="false")
})
public class TilesListModel {

    @Self
    private SlingHttpServletRequest request;

    @Self
    private Resource resource;

    // Inject a property name whose name does NOT match the Model field name
    // Since the Default inject strategy is OPTIONAL (set on the @Model), we can mark injections as @Required. @Optional can be used if the default strategy is REQUIRED.
    @ValueMapValue
    @Named("jcr:title")
    @Required
    private String title;

    // Inject a fields whose property name DOES match the model field name
    @ValueMapValue
    @Optional
    private String pageTitle;

    // Mark as Optional
    @ValueMapValue
    @Optional
    private String navTitle;

    // Provide a default value if the property name does not exist
    @ValueMapValue
    @Named("jcr:description")
    @Default(values = "No description provided")
    private String description;

    // Various data types can be injected
    @ValueMapValue
    @Named("jcr:created")
    private Calendar createdAt;

    @ValueMapValue
    @Default(booleanValues = false)
    boolean navRoot;

    // Inject OSGi services
    @OSGiService
    @Required
    private QueryBuilder queryBuilder;

    // Injection will occur over all Injectors based on Ranking;
    // Force an Injector using @Source(..)
    // If an Injector is not working; ensure you are using the latest version of Sling Models
    @SlingObject
    @Required
    private ResourceResolver resourceResolver;

    // Internal state populated via @PostConstruct logic
    private long size;
    private Page page;

    @PostConstruct
    // PostConstructs are called after all the injection has occurred, but before the Model object is returned for use.
    private void init() {
        // Note that @PostConstruct code will always be executed on Model instantiation.
        // If the work done in PostConstruct is expensive and not always used in the consumption of the model, it is
        // better to lazy-execute the logic in the getter and persist the result in  model state if it is requested again.
        page = resourceResolver.adaptTo(PageManager.class).getContainingPage(resource);

        final Map<String, String> map = new HashMap<String, String>();
        // Injected fields can be used to define logic
        map.put("path", page.getPath());
        map.put("type", "cq:Page");

        Query query = queryBuilder.createQuery(PredicateGroup.create(map), resourceResolver.adaptTo(Session.class));
        final SearchResult result = query.getResult();
        this.size = result.getHits().size();
    }

    /**
     * This getter wraps business logic around how an logic data point (title) is represented for this resource.
     *
     * @return The Page Title if exists, with fallback to the jcr:title
     */
    public String getTitle() {
        return StringUtils.defaultIfEmpty(pageTitle, title);
    }

    /**
     * This getter exposes data Injected into the Model and allows parameterized manipulation of the output.
     *
     * @param truncateAt length at which to truncate
     * @return the truncated description.
     */
    public String getDescription(int truncateAt) {
        if (this.description.length() > truncateAt) {
            return StringUtils.substring(this.description, truncateAt) + "...";
        } else {
            return this.description;
        }
    }

    /**
     * Default implementation of the parameterizable getDescription(..).
     *
     * @return the truncated description.
     */
    public String getDescription() {
        // This is just an example of including business logic in the Sling Model;
        return this.getDescription(100);
    }

    /**
     * This getter exposes the work of a @PostConstruct method.
     *
     * @return the number of cq:Pages that exist under this resource.
     */
    public long getSize() {
        return this.size;
    }

    /**
     * @return the created at Calendar value as Date.
     */

    // @JsonIgnore is a Jackson Annotation specific to this field that prevents this field from being serialized into the exported JSON.
    // For a list of Jackson Annotations see https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations
    @JsonIgnore
    public Calendar getCreatedAt() {
        return createdAt;
    }

    /**
     * @return the resource path to this content. Does not include the extension.
     */
    public String getPath() {
        return page.getPath();
    }

    public String getHelloWorld() {
        return "Hello World";
    }

    // @JsonProperty is a Jackson Annotation specific to this field defines the JSON Property name to expose this method as.
    // For a list of Jackson Annotations see https://github.com/FasterXML/jackson-annotations/wiki/Jackson-Annotations
    @JsonProperty(value = "goodbye-world")
    public String goodbyeWorld() {
        return "Goodbye World";
    }
}