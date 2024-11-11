/*
  Annotations for the Browse GenreHierarchy App
*/

using AdminService from '../../srv/admin-service';


annotate AdminService.GenreHierarchy with @Aggregation.RecursiveHierarchy#AdminHierarchy: {
    $Type: 'Aggregation.RecursiveHierarchyType',
    NodeProperty: node_id, // identifies a node
    ParentNavigationProperty: parent // navigates to a node's parent
  };

  annotate AdminService.GenreHierarchy with @Hierarchy.RecursiveHierarchy#AdminHierarchy: {
  $Type: 'Hierarchy.RecursiveHierarchyType',
  // ExternalKey           : null,
  LimitedDescendantCount: LimitedDescendantCount,
  DistanceFromRoot: DistanceFromRoot,
  DrillState: DrillState,
  Matched: Matched,
  MatchedDescendantCount: MatchedDescendantCount,
  LimitedRank: LimitedRank
};