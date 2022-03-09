package net.catenax.sdhub.util;

import java.net.URI;

/**
 * A DTO containing a raw key and associated Controller which controls that key
 */
public record KeyInfo(
        byte[] rawKey,
        URI controller) {
}
