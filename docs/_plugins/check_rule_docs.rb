
Jekyll::Hooks.register :site, :after_init do |site|
    ENV_VAR_NAME='PMD_DOC_IGNORE_MISSING_RULE_DOC'

    if ENV[ENV_VAR_NAME] then
        Jekyll.logger.warn "Not verifying that generated rule doc pages exist. The generated documentation might be incomplete!"
    else

        def check_file(filename)
            unless File.exist?(filename)
                Jekyll.logger.error "File #{filename} does not exist!"
                Jekyll.logger.abort_with "Please execute `./mvnw package -Pgenerate-rule-docs -pl pmd-doc` before"\
                  "generating pmd documentation or ignore by setting env variable #{ENV_VAR_NAME}"
            end
        end

        sourceDir = site.source

        Jekyll.logger.info "Verifying that generated rule doc pages exist in #{sourceDir}..."

        languages = ['apex', 'ecmascript', 'html', 'java', 'jsp', 'kotlin', 'modelica', 'plsql', 'pom',
            'swift', 'velocity', 'visualforce', 'xml', 'xsl']
        languages.each do |lang|
            check_file "#{sourceDir}/pages/pmd/rules/#{lang}.md"
        end

        # spot check of some categories
        categories = ['apex/bestpractices.md', 'apex/codestyle.md', 'java/bestpractices.md', 'java/codestyle.md', 'java/design.md', 'modelica/bestpractices.md']
        categories.each do |cat|
            check_file "#{sourceDir}/pages/pmd/rules/#{cat}"
        end
    end
end
