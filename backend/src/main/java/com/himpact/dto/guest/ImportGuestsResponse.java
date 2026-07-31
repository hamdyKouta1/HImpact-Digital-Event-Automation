package com.himpact.dto.guest;

import java.util.List;

/**
 * Summary returned after importing a CSV guest list per PO Requirement 5.
 */
public record ImportGuestsResponse(
        int totalProcessed,
        int successfullyImported,
        int skippedDuplicates,
        List<String> skipReasons,
        List<String> errors
) {}
