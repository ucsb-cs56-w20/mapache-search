package edu.ucsb.cs56.mapache_search.preview;

import edu.ucsb.cs56.mapache_search.search.Item;

public interface PreviewProviderService {
    String getPreviewType(Item item);
}
