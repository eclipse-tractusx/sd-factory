package org.eclipse.tractusx.selfdescriptionfactory.service;

import java.net.URI;
import java.util.Map;


public record Claims(Map<String, Object> claims, URI vocabulary) {
}
