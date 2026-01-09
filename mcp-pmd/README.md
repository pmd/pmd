# PMD MCP Server

A [Model Context Protocol (MCP)](https://modelcontextprotocol.io/) server that provides [PMD](https://pmd.github.io/) static code analysis capabilities to AI assistants.

## Features

- **`pmd_check`** - Run PMD static analysis on source files
- **`pmd_cpd`** - Detect copy-paste (duplicated) code
- **`pmd_list_languages`** - List supported programming languages
- **`pmd_list_rulesets`** - List available rulesets for analysis

## Requirements

- **Node.js 18+**
- **PMD** installed locally (via Homebrew, or in PATH)

### Installing PMD

```bash
# macOS (Homebrew)
brew install pmd

# Verify installation
pmd --version
```

## Installation

### For Windsurf / Cursor / VS Code

Add to your MCP configuration (`.mcp.json` or settings):

```json
{
  "mcpServers": {
    "pmd": {
      "command": "npx",
      "args": ["@springsoftware/pmd-mcp"]
    }
  }
}
```

### For Claude Desktop

Add to `~/Library/Application Support/Claude/claude_desktop_config.json`:

```json
{
  "mcpServers": {
    "pmd": {
      "command": "npx",
      "args": ["@springsoftware/pmd-mcp"]
    }
  }
}
```

## Usage Examples

### Run PMD Analysis

```
Use pmd_check to analyze /path/to/my/java/project for code quality issues
```

### Detect Duplicated Code

```
Use pmd_cpd to find duplicated Python code in /path/to/project with minimum 75 tokens
```

### List Available Rulesets

```
What PMD rulesets are available for Java?
```

## Tools Reference

### `pmd_check`

Run PMD static code analysis on source files.

**Parameters:**
- `path` (required) - Path to source file or directory
- `rulesets` - Array of rulesets to apply (default: quickstart)
- `language_version` - Language version (e.g., "java-21")
- `minimum_priority` - Minimum priority to report (1-5)
- `excludes` - File patterns to exclude

**Example:**
```json
{
  "path": "/Users/me/project/src",
  "rulesets": ["category/java/bestpractices.xml", "category/java/errorprone.xml"],
  "minimum_priority": 3
}
```

### `pmd_cpd`

Run copy-paste detection to find duplicated code.

**Parameters:**
- `path` (required) - Path to source file or directory
- `language` (required) - Programming language (python, java, typescript, etc.)
- `minimum_tokens` - Minimum token count for duplication (default: 50)
- `ignore_literals` - Ignore literal values
- `ignore_identifiers` - Ignore identifier names
- `excludes` - File patterns to exclude

**Example:**
```json
{
  "path": "/Users/me/project",
  "language": "python",
  "minimum_tokens": 75,
  "ignore_literals": true
}
```

### `pmd_list_languages`

List all supported programming languages.

### `pmd_list_rulesets`

List available rulesets, optionally filtered by language.

**Parameters:**
- `language` - Filter by language (e.g., "java", "apex")

## Supported Languages

### Static Analysis (pmd_check)
- Java, JavaScript (ECMAScript), Apex, Kotlin, Swift
- HTML, XML, XSL, JSP, Velocity
- PL/SQL, Modelica, Scala, Visualforce

### Copy-Paste Detection (pmd_cpd)
- Python, Java, JavaScript, TypeScript, Kotlin, Swift
- Go, Ruby, Rust, Scala, C/C++, C#, PHP
- And many more...

## Development

```bash
# Install dependencies
npm install

# Build
npm run build

# Run in development mode
npm run dev

# Run tests
npm test
```

## License

BSD-4-Clause (same as PMD)

## Links

- [PMD Documentation](https://docs.pmd-code.org/)
- [MCP Specification](https://modelcontextprotocol.io/)
- [PMD GitHub](https://github.com/pmd/pmd)
