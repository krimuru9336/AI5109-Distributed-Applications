import packageJSON from '../../package.json';
import fs from 'fs';
import path from 'path';

const pkg = {
    ...packageJSON,
    scripts: {
        "db_init": "node ./scripts/db.init.js",
        "start": "node server.js"
    },
    devDependencies: {}
}

fs.writeFileSync(path.resolve(__dirname, "./../package.json"), JSON.stringify(pkg), { encoding: 'utf-8' });
fs.rmSync(path.resolve(__dirname, '../../package.json'));

export { }