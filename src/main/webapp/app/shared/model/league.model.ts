import { ITeam } from 'app/shared/model/team.model';

export interface ILeague {
  id?: number;
  name?: string | null;
  teams?: ITeam[] | null;
}

export const defaultValue: Readonly<ILeague> = {};
