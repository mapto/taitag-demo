import { IDocument } from 'app/shared/model/document.model';

export interface IPerson {
  id?: number;
  name?: string | null;
  latinName?: string | null;
  aka?: string | null;
  birth?: number | null;
  death?: number | null;
  affiliation?: string | null;
  authors?: IDocument[] | null;
  recipients?: IDocument[] | null;
  quoteds?: IDocument[] | null;
}

export const defaultValue: Readonly<IPerson> = {};
