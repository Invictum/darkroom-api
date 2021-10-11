package com.github.darkroom.images.repository;

import okhttp3.Headers;

public record ImageDetails(Headers headers, byte[] data) {
}
