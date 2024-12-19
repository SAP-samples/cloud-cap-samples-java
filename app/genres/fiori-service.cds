/*
  UI annotations for the Browse GenreHierarchy App
*/

using AdminService from '../../srv/admin-service';


annotate AdminService.GenreHierarchy with @Aggregation.RecursiveHierarchy#GenreHierarchy: {
    $Type: 'Aggregation.RecursiveHierarchyType',
    NodeProperty: ID, // identifies a node
    ParentNavigationProperty: parent // navigates to a node's parent
  };

  annotate AdminService.GenreHierarchy with @Hierarchy.RecursiveHierarchy#GenreHierarchy: {
  $Type: 'Hierarchy.RecursiveHierarchyType',
  // ExternalKey           : null,
  LimitedDescendantCount: LimitedDescendantCount,
  DistanceFromRoot: DistanceFromRoot,
  DrillState: DrillState,
  Matched: Matched,
  MatchedDescendantCount: MatchedDescendantCount,
  LimitedRank: LimitedRank
};
