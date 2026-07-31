package com.himpact.dto.media;

import java.util.UUID;

public record SyncMediaResponse(
        UUID mediaSyncId,
        String localIdentifier,
        String syncStatus,
        boolean alreadyProcessed,
        MediaFileResponse mediaFile
) {}
