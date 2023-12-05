
export type BodyMap = Record<`body${number}`, string>;

export interface LayoutValues extends BodyMap {
    doc_title: string;
    // global_styles: string;
    // global_scripts: string;
    page_scripts: string;
    page_styles: string;
}