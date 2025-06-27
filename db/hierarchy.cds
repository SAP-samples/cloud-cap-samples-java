namespace my.common;

aspect Hierarchy {
  virtual LimitedDescendantCount : Integer64;
  virtual DistanceFromRoot       : Integer64;
  virtual DrillState             : String;
  virtual LimitedRank            : Integer64;
}


annotate Hierarchy with @Capabilities.FilterRestrictions.NonFilterableProperties: [
  'LimitedDescendantCount',
  'DistanceFromRoot',
  'DrillState',
  'LimitedRank'
];

annotate Hierarchy with @Capabilities.SortRestrictions.NonSortableProperties: [
  'LimitedDescendantCount',
  'DistanceFromRoot',
  'DrillState',
  'LimitedRank'
];
