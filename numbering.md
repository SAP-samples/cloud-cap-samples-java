# Genre Hierarchy Numbering

## Overview

The `GenreHierarchy` entity in the Admin Service represents a tree of genres using a parent-child relationship. Each node has a `siblingRank` that determines the sort order among siblings, and a computed `number` that provides a human-readable, dot-separated path from root to node (e.g. `2.20.1`).

## Concepts

### siblingRank

`siblingRank` is a persisted integer on each genre node. It defines the display order of sibling nodes (children of the same parent). Ranks are contiguous and 1-based within each parent group:

```
Fiction          (siblingRank: 1)
Non-Fiction      (siblingRank: 2)
  Memoir         (siblingRank: 20)
    Cooking Memoir   (siblingRank: 1)
    Graphic Memoir   (siblingRank: 2)
    Parenting Memoir (siblingRank: 3)
```

### number

`number` is a virtual (non-persisted) element computed on read by an `@After` READ handler. It concatenates the `siblingRank` values along the ancestor path from root to node, separated by dots:

- Fiction -> `1`
- Non-Fiction -> `2`
- Memoir (child of Non-Fiction) -> `2.20`
- Cooking Memoir (child of Memoir) -> `2.20.1`

The computation loads only the ancestors actually needed from the database, one query per tree level, making it efficient for large hierarchies.

### moveSibling Action

`moveSibling` is a bound action on `GenreHierarchy` that implements the OData `ChangeNextSiblingAction` ([spec](https://github.com/SAP/odata-vocabularies/blob/main/vocabularies/Hierarchy.md#template_changenextsiblingaction-experimental)). It is invoked by the Fiori Elements tree table when a user reorders nodes via drag-and-drop within the same parent.

**Parameters:**
- **Binding parameter**: The node being moved (T).
- **NextSibling**: The node that should become T's next sibling after the move, or `null` to move T to the end.

**Algorithm:** Uses expression-based SQL updates to shift only the affected sibling ranks, rather than loading and rewriting all siblings. This is O(affected siblings) instead of O(all siblings).

## Changes

### 1. Integration tests for HierarchySiblingActionHandler (`1ae5948`)

Added `HierarchySiblingActionHandlerTest` with 4 test cases covering the `moveSibling` action:

- Move a node forward (before a later sibling)
- Move a node to the last position (NextSibling = null)
- Move a node backward (before an earlier sibling)
- Move a node to the first position

### 2. Contiguous 1-based siblingRank values (`eca3e5f`)

Renumbered `siblingRank` values in the CSV test data from arbitrary non-contiguous values to contiguous 1-based values per parent group. Adjusted the handler and tests accordingly. This is a prerequisite for the expression-based update optimization.

### 3. Optimized moveSibling handler (`0ebed83`)

Replaced the original algorithm (load all siblings into a Java list, reorder, write all back) with targeted expression-based SQL updates:

- `SET siblingRank = siblingRank - 1` to close the gap left by the moved node
- `SET siblingRank = siblingRank + 1` to open space at the target position
- One direct update to set the moved node's new rank

Uses `CQL.count()` for the "move to end" case and includes an early exit when the node is already at the target position.

### 4. Virtual `number` element (`f7d50d2`)

- Added `virtual number : String` to the `Genres` CDS model.
- Implemented `HierarchyNumberHandler` (`@After` READ) that computes the dot-separated number by walking up the ancestor chain.
- The handler loads only the needed ancestors (not the full table), one query per tree level.
- Includes a `@Before` READ workaround that ensures `siblingRank` is always selected (to work around a cds4j SQL rendering bug).
- Added 7 tests covering root nodes, intermediate nodes, leaf nodes, `$count` queries, and collection reads.

### 5. siblingRank maintenance on reparent and delete (`7eef849`)

Added `HierarchyMaintainRanksHandler` that maintains contiguous `siblingRank` values when:

- **A node is reparented** (PATCH with changed `parent_ID` or `parent` association): The moved node gets rank 1 under the new parent. All existing children of the new parent are shifted up by 1. The gap in the old parent's children is closed.
- **A node is deleted**: The gap left by the deleted node is closed among its remaining siblings.

Handles both flat foreign key (`parent_ID`) and nested association (`parent: { ID: ... }`) payloads.

Added 6 tests covering reparent (first node, middle node, via nested association) and delete (middle node, last node).

### 6. UI improvements (`270e476`)

- Added `@Common.SideEffects` annotation on `GenreHierarchy` with `SourceProperties: [parent]` to trigger a tree refresh after reparenting a node via drag-and-drop.
- Added `number` and `siblingRank` columns to the `@UI.LineItem` of the admin "Manage Genres" tree view.

## File Summary

| File | Purpose |
|------|---------|
| `db/books.cds` | Added `virtual number : String` to `Genres` entity |
| `db/data/my.bookshop-Genres.csv` | Renumbered `siblingRank` to contiguous 1-based values |
| `srv/admin-service.cds` | Added `@Common.SideEffects` for tree refresh on reparent |
| `srv/.../HierarchySiblingActionHandler.java` | Optimized moveSibling with expression-based updates |
| `srv/.../HierarchyNumberHandler.java` | Computes virtual `number` on read, ensures `siblingRank` is selected |
| `srv/.../HierarchyMaintainRanksHandler.java` | Maintains siblingRank on reparent and delete |
| `srv/.../HierarchySiblingActionHandlerTest.java` | 4 tests for moveSibling action |
| `srv/.../HierarchyNumberHandlerTest.java` | 7 tests for number computation |
| `srv/.../HierarchyMaintainRanksHandlerTest.java` | 6 tests for reparent and delete |
| `app/admin/fiori-service.cds` | Added number and siblingRank to admin tree view LineItem |

## Future: Database-Level Computation of `number`

The `number` is currently computed in a Java handler. For better performance, it could be computed at the database level:

- **H2, PostgreSQL, SQLite**: Recursive CTE that walks the ancestor chain and concatenates `siblingRank` values.
- **SAP HANA**: `HIERARCHY_ANCESTORS_AGGREGATE` function with `STRING_AGG(rankStr, '.')` to aggregate ancestor ranks top-down.

This would eliminate the per-request ancestor lookups and push the computation to a single SQL query.
