export interface MatchDTO {
  leaderVin: string;
  followerVin: string;
  requiredLane: number;
  requiredSpeed: number;
  startTime: Date;
  allowedSpeedDiscrepancy: number;
  timeDiscrepancy: number;
}
