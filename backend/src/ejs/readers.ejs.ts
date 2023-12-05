import { TEMPLATES_DIR } from '../templates/index';
import fs from 'fs';
import path from 'path';

/**
 * Reads files contents in `templates/` directory and returns resulting string.
 * @param tDir 
 * @param files 
*/
export const readEJSContent = (tDir: string, files: string[]) => {

    let rendered = '';
    files.forEach(f => {
        const content = fs.readFileSync(path.resolve(TEMPLATES_DIR + tDir, f), { encoding: 'utf-8' });
        rendered += content + '\t';
    });
    return rendered;
}

/**
 * accepts the name of a view file to read.                                       
 * returns the output string.
 */
export const readView = (view: string) => {
    return readEJSContent('/views', [view]);
}
/**
 * accepts the name of a layout file to read.                                       
 * returns the output string.
 */
export const readLayout = (layout: string) => {
    return readEJSContent('/layouts', [layout]);
}

/**
 * Accepts an array of JS files to read.
 */
export const readJSFiles = (files: string[]) => {
    return readEJSContent('/js', files);
}
/**
 * Accepts an array of CSS files to read.
 */
export const readCSSFiles = (files: string[]) => {
    return readEJSContent('/css', files);
}


/**
 * reading CSS files, that should be present in all HTML files.                         
 ** register global CSS files here.
 */
export const renderGlobalCSS = () => {
    const css_globals = ['global.css'];
    return readCSSFiles(css_globals);
}
/**
 * reading JS files, that should be present in all HTML files.                                                          
 ** Register global JS files here.
 */
export const renderGlobalJS = () => {
    const js_globals = ['global.js'];
    return readJSFiles(js_globals);
}
