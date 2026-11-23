using {CatalogService} from './cat-service';

// The action deliberately does not reference Countries in CDS. Only its Java
// implementation does, so sidecar minification cannot see that dependency.
extend service CatalogService with {
    action probeMinifiedCountries() returns Integer;
}
