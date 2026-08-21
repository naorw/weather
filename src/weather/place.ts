import type { Coordinates } from "./models";

/** Provider-normalized search hit. Not a persisted city record. */
export type PlaceCandidate = {
  displayName: string;
  coordinates: Coordinates;
  country?: string;
  region?: string;
};
