import { ILeague } from 'app/shared/model/league.model';

export interface ITeam {
  id?: number;
  name?: string | null;
  league?: ILeague | null;
}

export const defaultValue: Readonly<ITeam> = {};
