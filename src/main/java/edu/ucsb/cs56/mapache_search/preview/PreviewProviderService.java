package edu.ucsb.cs56.mapache_search.preview;

import edu.ucsb.cs56.mapache_search.entities.Item;

public interface PreviewProviderService {
    String getPreviewType(Item item);
}
