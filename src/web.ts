import { WebPlugin } from '@capacitor/core';

import type { InAppBrowserPlugin } from './definitions';

export class InAppBrowserWeb extends WebPlugin implements InAppBrowserPlugin {
  async open(options: { url: string; title?: string }): Promise<void> {
    // TODO:
  }
  async close(): Promise<void> {
    // TODO:
  }
  async postMessage(options: any): Promise<void> {
    // TODO:
  }
}
