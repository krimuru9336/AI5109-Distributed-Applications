import { BodyMap, LayoutValues } from "../types/engine.types";
import ejs, { Options } from 'ejs';
import { readLayout, readView, renderGlobalCSS, renderGlobalJS } from "./readers.ejs";

const global_styles = renderGlobalCSS();
const global_scripts = renderGlobalJS();

export const renderLayout =
    (layoutFile: string,
        bodies: BodyMap,
        layout_values: LayoutValues,
        opts: Options = { async: false }) => {
        const output_str = ejs.render(
            readLayout(layoutFile),
            {
                global_scripts,
                global_styles,
                ...bodies,
                ...layout_values
            },
            opts) as string;
        return output_str;
    }


export const renderView = (view: string, data: Record<string, any>, opts: Options = { async: false }) => {
    return ejs.render(readView(view), data, opts) as string;
}