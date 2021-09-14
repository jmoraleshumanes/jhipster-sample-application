import dayjs from 'dayjs';
import { ITeam } from 'app/shared/model/team.model';
import { Position } from 'app/shared/model/enumerations/position.model';
import { Foot } from 'app/shared/model/enumerations/foot.model';

export interface IPlayer {
  id?: number;
  name?: string | null;
  country?: string | null;
  age?: number | null;
  position?: Position | null;
  foot?: Foot | null;
  signed?: string | null;
  contractUntil?: string | null;
  value?: number | null;
  team?: ITeam | null;
}

export const defaultValue: Readonly<IPlayer> = {};
