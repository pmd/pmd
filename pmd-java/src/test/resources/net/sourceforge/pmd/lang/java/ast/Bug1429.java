public class Bug1429 {
    public Set<String> getAttributeTuples() {
        return (Set<String>) (this.attributes == null ? Collections.<String> emptySet() : new HashSet<String>(
                CollectionUtils.collect(this.attributes.keySet(), new Transformer() {
                    @Override
                    public Object transform(final Object obj) {
                        final String key = (String) obj;
                        final String value = HGXLIFFTypeConfiguration.this.attributes.get(key);

                        String result = key;
                        if (StringUtils.isNotEmpty(value)) {
                            result = result.concat(":").concat(value);
                        }
                        return result;
                    }
                })));
    }
}