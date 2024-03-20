import { WebPlugin } from '@capacitor/core';
import type { InAppBrowserPlugin } from './definitions';
export declare class InAppBrowserWeb extends WebPlugin implements InAppBrowserPlugin {
    open(options: {
        url: string;
        title?: string;
    }): Promise<void>;
    close(): Promise<void>;
    postMessage(options: any): Promise<void>;
}
