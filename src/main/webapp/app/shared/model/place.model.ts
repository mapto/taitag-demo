import { IDocument } from 'app/shared/model/document.model';

export interface IPlace {
  id?: number;
  city?: string;
  italianName?: string | null;
  placeIns?: IDocument[] | null;
  placeOuts?: IDocument[] | null;
}

export const defaultValue: Readonly<IPlace> = {};
