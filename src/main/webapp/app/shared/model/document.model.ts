import dayjs from 'dayjs';
import { IPerson } from 'app/shared/model/person.model';
import { IPlace } from 'app/shared/model/place.model';
import { DocumentType } from 'app/shared/model/enumerations/document-type.model';
import { Archive } from 'app/shared/model/enumerations/archive.model';
import { Field } from 'app/shared/model/enumerations/field.model';

export interface IDocument {
  id?: string;
  georgianDate?: string | null;
  type?: DocumentType | null;
  incipit?: string | null;
  transcription?: string | null;
  collection?: string | null;
  archive?: Archive | null;
  folder?: string | null;
  folderN?: string | null;
  shelfMark?: string | null;
  publication?: string | null;
  field?: Field | null;
  written?: IPerson | null;
  arrived?: IPlace | null;
  originated?: IPlace | null;
  receiveds?: IPerson[] | null;
  quotedIns?: IPerson[] | null;
}

export const defaultValue: Readonly<IDocument> = {};
