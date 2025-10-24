---
title: search
layout: none
search: exclude
---
{%- capture comma -%},
{%- endcapture -%}

document.addEventListener("DOMContentLoaded", (event) => {

let pmd_doc_search_index = [
{% assign sorted_pages = site.pages | sort: "path" | where_exp:"item", "item.search != 'exclude'" -%}
{%- for page in sorted_pages -%}
    {%- if page.permalink contains "pmd_rules_" and page.keywords -%}
      {%- assign rules = page.keywords | split: ", " -%}
      {%- assign ruleset = rules[0] -%}
{
  "type": "ruledoc ruleset",
  "source": "{{ page.path }}",
  "title": "{{ ruleset | escape | escape_json }} ({{page.language}}, {{page.title}})",
  "tags": "{{ page.tags | escape_json }}",
  "keywords": "{{ ruleset | escape_json }}",
  "url": "{{ page.url | remove: "/"}}",
  "summary": "{{ page.summary | strip | escape_json | default: ' ' }}"
},
        {%- for rule in rules offset:1 -%}
{
  "type": "ruledoc",
  "source": "{{ page.path }}",
  "title": "{{ rule | escape | escape_json }} ({{page.language}}, {{page.title}})",
  "tags": "{{ page.tags | escape_json }}",
  "keywords": "{{ rule | separate_words | escape_json }}",
  "url": "{{ page.url | remove: "/"}}#{{ rule | downcase }}",
  "summary": "{{ page.rules[rule] | truncatewords: 15 | strip | escape_json | default: ' ' }}"
}
        {%- unless forloop.last -%}{{ comma }}{%- endunless -%}
      {%- endfor -%}
    {%- else -%}
{
  "type": "page",
  "source": "{{ page.path }}",
  "title": "{{ page.title | escape | escape_json }}",
  "tags": "{{ page.tags | escape_json }}",
  "keywords": "{{ page.keywords | escape_json }}",
  "url": "{{ page.url | remove: "/"}}",
  "summary": "{{ page.summary | strip | escape_json | default: ' ' }}"
}
    {%- endif -%}
    {%- unless forloop.last or site.posts.size > 0 -%}{{ comma }}{%- endunless -%}
{%- endfor -%}
{%- for post in site.posts -%}
{
  "type": "post",
  "source": "{{ post.path }}",
  "title": "{{ post.title | escape | escape_json }}",
  "tags": "{{ post.tags  | escape_json }}",
  "keywords": "{{ post.keywords | escape_json }}",
  "url": "{{ post.url | remove: "/" }}",
  "summary": "{{ post.summary | strip | escape_json | default: ' ' }}"
}
  {%- unless forloop.last -%}{{ comma }}{%- endunless -%}
{%- endfor %}
];


    // Initialize jekyll search in topnav.
    SimpleJekyllSearch({
        searchInput: document.getElementById('search-input'),
        resultsContainer: document.getElementById('results-container'),
        json: pmd_doc_search_index,
        searchResultTemplate: '<li><a href="{url}"><strong>{title}</strong><br>{summary}</a></li>',
        noResultsText: '<li>No results found.</li>',
        limit: 20,
        fuzzy: false,
    });
    // Make sure to close and empty the search results after clicking one result item.
    // This is necessary, if we don't switch the page but only jump to a anchor on the
    // same page.
    document.getElementById('results-container').addEventListener('click', e => {
        document.getElementById('search-input').value = '';
        e.target.innerHTML = '';
    });
    // simple keyboard control of search results
    document.querySelectorAll('#search-input, body').forEach(element => {
        element.addEventListener('keyup', e => {
            if (e.key !== 'ArrowDown' && e.key !== 'ArrowUp') {
                return;
            }
            if (document.querySelectorAll('#results-container li').length === 0) {
                return;
            }

            let current = document.querySelector('#results-container li.selected');
            if (!current) {
                current = document.querySelector('#results-container li');
            } else {
                current.classList.remove('selected');
                if (e.key === 'ArrowDown') {
                    if (current.nextSibling != null) {
                        current = current.nextSibling;
                    }
                } else if (e.key === 'ArrowUp') {
                    if (current.previousSibling !== null) {
                        current = current.previousSibling;
                    }
                }
            }
            current.classList.add('selected');
            current.querySelector('a').focus();
            e.preventDefault();
            e.stopImmediatePropagation(); // avoid triggering another search and rerender the results
        });
    });
    document.getElementById('results-container').addEventListener('mouseover', e => {
        let selected = document.getElementById('results-container').querySelector('li.selected')
        if (selected) {
            selected.classList.remove('selected');
        }
        let newSelected = e.target.closest('li');
        if (newSelected) {
            newSelected.classList.add('selected');
            if (document.activeElement !== document.getElementById('search-input')) {
                newSelected.querySelector('a').focus();
            }
        }
    });
    document.body.addEventListener('keyup', e => {
        if (e.key === 's') {
            document.getElementById('search-input').focus();
        }
        if (e.key === 'Escape') {
            document.getElementById('results-container').innerHTML = '';
        }
    });
    document.body.addEventListener('click', e => {
        const resultsContainer = document.getElementById('results-container');
        if (resultsContainer.querySelectorAll('li').length > 0) {
            resultsContainer.innerHTML = '';
        }
    });

});
