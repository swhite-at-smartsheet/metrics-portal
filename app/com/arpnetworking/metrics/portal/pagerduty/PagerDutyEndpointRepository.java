package com.arpnetworking.metrics.portal.pagerduty;


import models.internal.*;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface for repository of pagerduty endpoints.
 *
 * @author Sheldon White (sheldon.white at smartsheet dot com)
 */
public interface PagerDutyEndpointRepository {

    /**
     * Open the <code>PagerDutyEndpointRepository</code>.
     */
    void open();

    /**
     * Close the <code>PagerDutyEndpointRepository</code>.
     */
    void close();

    /**
     * Get the <code>Alert</code> by identifier.
     *
     * @param identifier The <code>PagerDutyEndpoint</code> identifier.
     * @return The matching <code>PagerDutyEndpoint</code> if found or <code>Optional.empty()</code>.
     */
    Optional<PagerDutyEndpoint> get(UUID identifier);

    /**
     * Delete an <code>Alert</code> by identifier.
     *
     * @param identifier The <code>PagerDutyEndpoint</code> identifier.
     * @return The matching <code>PagerDutyEndpoint</code> if found or <code>Optional.empty()</code>.
     */
    int delete(UUID identifier);

    /**
     * Create a query against the pagerduty endpoints repository.
     *
     * @return Instance of <code>PagerDutyEndpointQuery</code>.
     */
    PagerDutyEndpointQuery createQuery();

    /**
     * Query pagerduty endpoints.
     *
     * @param query Instance of <code>PagerDutyEndpointQuery</code>.
     * @return The <code>Collection</code> of all pagerduty endpoints.
     */
    QueryResult<PagerDutyEndpoint> query(final PagerDutyEndpointQuery query);
//
//    /**
//     * Retrieve the total number of alerts in the repository.
//     *
//     * @param organization The organization owning the alerts.
//     * @return The total number of alerts.
//     */
//    long getAlertCount(Organization organization);

    /**
     * Add a new pagerduty endpoint or update an existing one in the repository.
     *
     * @param pagerDutyEndpoint The endpoint to add to the repository.
     */
    void addOrUpdatePagerDutyEndpoint(PagerDutyEndpoint pagerDutyEndpoint);
}
