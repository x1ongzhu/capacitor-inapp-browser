export interface InAppBrowserPlugin {
    open(options: {
        url: string;
        title?: string;
    }): Promise<void>;
    close(): Promise<void>;
    postMessage(options: any): Promise<void>;
}
