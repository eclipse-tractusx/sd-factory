package net.catenax.sdhub.util;

import java.net.URI;

public record KeyInfo(
        byte[] rawKey,
        URI controller) {
}
