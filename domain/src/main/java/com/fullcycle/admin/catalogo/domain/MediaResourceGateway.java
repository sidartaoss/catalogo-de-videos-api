package com.fullcycle.admin.catalogo.domain;

import com.fullcycle.admin.catalogo.domain.resource.Resource;
import com.fullcycle.admin.catalogo.domain.video.*;

import java.util.Optional;

public interface MediaResourceGateway {

    AudioVideoMedia storeAudioVideo(VideoID anId, VideoResource aResource);

    ImageMedia storeImage(VideoID anId, VideoResource aResource);

    Optional<Resource> getResource(VideoID anId, VideoMediaType type);

    void clearResources(VideoID anId);
}
