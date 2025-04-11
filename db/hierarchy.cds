namespace my.common;

aspect Hierarchy {
  virtual LimitedDescendantCount : Integer64;
  virtual DistanceFromRoot       : Integer64;
  virtual DrillState             : String;
  virtual Matched                : Boolean; // not used
  virtual MatchedDescendantCount : Integer64; // not used
  virtual LimitedRank            : Integer64;
}


annotate Hierarchy with @Capabilities.FilterRestrictions.NonFilterableProperties: [
  'LimitedDescendantCount',
  'DistanceFromRoot',
  'DrillState',
  'Matched',
  'MatchedDescendantCount',
  'LimitedRank'
];

annotate Hierarchy with @Capabilities.SortRestrictions.NonSortableProperties: [
  'LimitedDescendantCount',
  'DistanceFromRoot',
  'DrillState',
  'Matched',
  'MatchedDescendantCount',
  'LimitedRank'
];
