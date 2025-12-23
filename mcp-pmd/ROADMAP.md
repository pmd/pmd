# PMD MCP Server - Implementation Roadmap

## Project Goal

Create an MCP (Model Context Protocol) server for PMD that allows AI assistants (Claude, Copilot, Cursor, Windsurf, etc.) to run PMD static code analysis directly. The server will use **stdio transport** (like Playwright MCP) for local execution without requiring a separate server process.

## Architecture Overview

```
┌─────────────────┐     stdio      ┌─────────────────┐     spawn      ┌─────────────┐
│   AI Client     │ ◄────────────► │   MCP Server    │ ◄────────────► │  PMD CLI    │
│ (Claude/Cursor) │   JSON-RPC     │  (TypeScript)   │   subprocess   │  (Java)     │
└─────────────────┘                └─────────────────┘                └─────────────┘
```

## Tools to Implement

### Phase 1: Core Analysis Tools
| Tool | Description | Priority |
|------|-------------|----------|
| `pmd_check` | Run PMD analysis on files/directories | P0 |
| `pmd_cpd` | Run copy-paste detection | P0 |
| `pmd_list_rulesets` | List available rulesets for a language | P1 |
| `pmd_list_languages` | List supported languages | P1 |

### Phase 2: Advanced Tools
| Tool | Description | Priority |
|------|-------------|----------|
| `pmd_analyze_rule` | Get details about a specific rule | P2 |
| `pmd_suppress` | Add suppression comment to code | P2 |
| `pmd_create_ruleset` | Generate custom ruleset XML | P2 |

---

## Implementation Steps

### Step 1: Project Setup ✅ [DONE]
- [x] Create `mcp-pmd` directory in PMD fork
- [x] Document MCP concepts and references
- [x] Initialize npm project with TypeScript
- [x] Add dependencies (@modelcontextprotocol/sdk, zod)
- [x] Configure TypeScript and build scripts

### Step 2: Basic Server Structure ✅ [DONE]
- [x] Create main server entry point (`src/index.ts`)
- [x] Set up stdio transport
- [x] Implement server initialization and capability negotiation
- [x] Add proper error handling and logging (to stderr)

### Step 3: PMD CLI Wrapper ✅ [DONE]
- [x] Create PMD executor module (`src/pmd-executor.ts`)
- [x] Detect PMD installation (homebrew, local, PATH)
- [x] Implement command builder for PMD CLI
- [x] Parse PMD output (text, JSON, XML formats)
- [x] Handle errors and timeouts

### Step 4: Implement `pmd_check` Tool ✅ [DONE]
- [x] Define input schema (path, language, rulesets, format)
- [x] Implement tool handler
- [x] Format results for LLM consumption
- [ ] Add support for incremental analysis
- [ ] Test with various languages (Java, JS, Python CPD)

### Step 5: Implement `pmd_cpd` Tool ✅ [DONE]
- [x] Define input schema (path, language, minimum-tokens)
- [x] Implement CPD execution
- [x] Parse and format duplication results
- [ ] Test with multiple languages

### Step 6: Implement Helper Tools ✅ [DONE]
- [x] `pmd_list_rulesets` - Query available rulesets
- [x] `pmd_list_languages` - List supported languages
- [ ] Add caching for ruleset/language info

### Step 7: CLI and Distribution ✅ [DONE]
- [x] Create CLI entry point (`cli.js`)
- [x] Add `--version`, `--help` flags
- [x] Configure npm package for publishing
- [x] Add bin entry for npx execution
- [ ] Test with various MCP clients (Windsurf, Cursor, Claude Desktop)

### Step 8: Documentation and Testing ✅ [PARTIAL]
- [x] Write comprehensive README
- [x] Add usage examples for each client
- [ ] Create integration tests
- [ ] Add GitHub Actions for CI/CD

### Step 9: Advanced Features (Phase 2)
- [ ] Rule details and documentation lookup
- [ ] Suppression comment generation
- [ ] Custom ruleset creation
- [ ] Configuration file support

---

## Technical Decisions

### Language: TypeScript
- Matches MCP SDK ecosystem
- Easy npm distribution
- Type safety for tool schemas

### PMD Execution Strategy
1. **Primary**: Use system-installed PMD (`pmd` command)
2. **Fallback**: Download PMD distribution if not found
3. **Output Format**: JSON for structured parsing, fallback to text

### Output Formatting
Results will be formatted as structured text optimized for LLM consumption:
```
## PMD Analysis Results

**Files Analyzed**: 15
**Violations Found**: 7

### High Priority (3)
- `src/Main.java:45` - AvoidDuplicateLiterals: The String "error" appears 4 times
- `src/Utils.java:23` - EmptyCatchBlock: Avoid empty catch blocks

### Medium Priority (4)
...
```

### Error Handling
- Graceful degradation if PMD not installed
- Clear error messages for missing dependencies
- Timeout handling for large codebases

---

## File Structure

```
mcp-pmd/
├── src/
│   ├── index.ts          # Main server entry point
│   ├── tools/
│   │   ├── pmd-check.ts  # PMD analysis tool
│   │   ├── pmd-cpd.ts    # Copy-paste detection tool
│   │   └── pmd-list.ts   # List rulesets/languages
│   ├── pmd-executor.ts   # PMD CLI wrapper
│   ├── output-parser.ts  # Parse PMD output
│   └── types.ts          # TypeScript types
├── cli.js                # CLI entry point
├── package.json
├── tsconfig.json
├── README.md
├── MCP_DOCS.md           # MCP reference docs
└── ROADMAP.md            # This file
```

---

## Success Criteria

1. **Works with Windsurf**: Can be added to `.mcp.json` and used in chat
2. **Zero Config**: `npx @pmd/mcp` just works if PMD is installed
3. **Useful Output**: Results are actionable and well-formatted for LLMs
4. **Fast**: Analysis completes in reasonable time (<30s for typical projects)
5. **Reliable**: Proper error handling, no crashes

---

## Next Action

**Start Step 1**: Initialize the npm project and set up TypeScript configuration.
